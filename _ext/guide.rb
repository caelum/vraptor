module Awestruct
  module Extensions
    module Guide
      Change = Struct.new(:sha, :author, :date, :message)

      class Index
        include Guide
        
        def initialize(path_prefix, num_changes = nil)
          @path_prefix = path_prefix
          @num_changes = num_changes
        end

        def transform(transformers)
          transformers << WrapHeaderAndAssignHeadingIds.new
        end

        def execute(site)
          guides = []
          
          site.pages.each do |page|
            if ( page.relative_source_path =~ /^#{@path_prefix}\/(?!index)/ )
              
              guide = OpenStruct.new
              page.guide = guide
              site.engine.set_urls([page])
              guide.url = page.url
              guide.title = page.title
              if page.description.nil?
                page.description = page.guide_summary
              end
              guide.summary = page.description
              
              # FIXME contributors should be listed somewhere on the page, but not automatically authors
              # perhaps as little pictures like on github

              # Add the Authors to Page and Guide based on Git Commit history
              #git_page_contributors = page_contributors(page, @num_changes)
              #if not page.authors
              #  page.authors = git_page_contributors
              #end
              #guide.authors = page.authors

              guide.changes = page_changes(page, @num_changes)
              
              # NOTE page.content forces the source path to be rendered
              page_content = Hpricot(page.content)
              chapters = []

              page_content.search('h3').each do |header_html|
                chapter = OpenStruct.new
                chapter.text = header_html.inner_html
                # FIXME we need a better way to generate link ids
                chapter.link_id = chapter.text.gsub(' ', '_').gsub('&#8217;', '_').gsub(/[\(\)\.!]/, '').downcase
                chapters << chapter
              end

              # make "extra chapters" a setting of the extension?
              chapter = OpenStruct.new
              chapter.text = 'Share the Knowledge'
              chapter.link_id = 'share'
              chapters << chapter

              guide.chapters = chapters

              page_languages = findLanguages(page)
              page.languages = page_languages if page_languages.size > 0

              guide.languages = page.languages

              # only add the main guide to the guide index (i.e., it doesn't have a locale suffix)
              if !(page.relative_source_path =~ /.*_[a-z]{2}(_[a-z]{2})?\..*/)
                guide.group = page.guide_group
                guide.order = if page.guide_order then page.guide_order else 100 end
                # default guide language is english
                guide.language = site.languages.en
                guides << guide
              end
            end
          end
          
          site.guides = guides
        end

        def findLanguages(page)
          languages = []
          base_page = page.source_path.gsub('.md', '').gsub(@path_prefix, '').gsub(/\/.*\//, '')
          #puts "Current Base Page #{base_page}"
          Dir.entries(@path_prefix[1..-1]).each do |x|
            if x =~ /(#{base_page})_([a-z]{2}(_[a-z]{2})?)\.(.*)/

              trans_base_name = $1
              trans_lang = $2
              trans_postfix = $4
              #puts "#{trans_base_name} #{trans_lang} #{trans_postfix}"

              trans_page = page.site.pages.find{|e| e.source_path =~ /.*#{trans_base_name}_#{trans_lang}.#{trans_postfix}/}

              trans_page.language_parent = page
              trans_page.language = page.site.languages.send(trans_lang)
              trans_page.language.code = trans_lang
              if !trans_page.translators.nil?
                trans_page.translators.each do |username|
                  page.site.identities.lookup(username).translator = true
                end
              end

              languages << trans_page
            end
          end
          return languages.sort{|a,b| a.language.code <=> b.language.code }
        end
      end

      class WrapHeaderAndAssignHeadingIds
      
        def transform(site, page, rendered)
          if page.guide
            page_content = Hpricot(rendered)

            guide_root = page_content.at('div[@id=guide]')

            # Wrap <div class="header"> around the h2 section
            # If you can do this more efficiently, feel free to improve it
            guide_content = guide_root.search('h2').first.parent
            indent = get_indent(get_depth(guide_content) + 2)
            in_header = true
            header_children = []
            guide_content.each_child do |child|
              if in_header
                if child.name == 'h3' or (child.name == 'div' and child.attributes['class'] == 'section')
                  in_header = false
                else
                  if child.pathname == 'text()' and child.to_s.strip.length == 0
                    header_children << Hpricot::Text.new("\n" + indent)
                  else
                    header_children << child
                  end
                end
              end
            end

            guide_header = Hpricot::Elem.new('div', {:class=>'header'})
            guide_content.children[0, header_children.length] = [guide_header]
            guide_header.children = header_children
            guide_content.insert_before(Hpricot::Text.new("\n" + indent), guide_header)
            guide_content.insert_after(Hpricot::Text.new("\n" + indent), guide_header)

            guide_root.search('h3').each do |header_html|
              page.guide.chapters.each do |chapter|
                if header_html.inner_html.eql? chapter.text
                  header_html.attributes['id'] = chapter.link_id
                  break
                end
              end
            end
            return page_content.to_html.gsub(/^<!DOCTYPE [^>]*>/, '<!DOCTYPE html>')
          end
          return rendered
        end
        
        def get_depth(node)
          depth = 0
          p = node
          while p.name != 'html'
            depth += 1
            p = p.parent
          end
          depth
        end

        def get_indent(depth, ts = '  ')
          "#{ts * depth}"
        end
        
      end

      ##
      # Returns a Array of unique author.name's based on the Git commit history located 
      # at page.site.dir for the given page. 
      # The Array is ordered by number of commits done by the authors.
      #
      def page_contributors(page, size)
        authors = Hash.new
        
        g = Git.open(page.site.dir)
        g.log(size).path(page.relative_source_path[1..-1]).each do |c|
          if authors[c.author.name]
            authors[c.author.name] = authors[c.author.name] + 1
          elsif
            authors[c.author.name] = 1
          end
        end
        return authors.sort{|a, b| b[1] <=> a[1]}.map{|x| x[0]}
      end

      def page_changes(page, size)
        changes = []
        g = Git.open(page.site.dir)
        g.log(size).path(page.relative_source_path[1..-1]).each do |c|
          changes << Change.new(c.sha, c.author.name, c.author.date, c.message.split(/\n/)[0].chomp('.').capitalize)
        end
        if changes.length == 0
          changes << Change.new('UNTRACKED', 'You', Time.now, 'Not yet committed')
        end
        changes
      end
    end
  end
end
