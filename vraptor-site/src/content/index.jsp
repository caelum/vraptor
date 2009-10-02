<%@include file="/header.jsp" %>
<div id="bannerWrap">
    	<div id="banner">
            
            <div id="floatBoxBanner">
            	<img src="images/iconOpenSource-trans.png" id="openSourceIcon" />
	            <h1>Experimente o novo vraptor 3</h1>
	            <ul>
	            	<li>Framework MVC Java para Web focado em desenvolvimento rápido</li>
	            	<li>Grande comunidade de usuários e desenvolvedores</li>
	            	<li>Ampla documentação disponível em Português</li>
    	        </ul>
       	     	<a href="download.jsp" id="downloadBtn2"><span>Download</span></a>
                <p><a href="documentacao/">Saiba mais sobre o vraptor</a> </p>
			</div><!-- content-float -->
            <img src="images/boxVraptor-trans.png" />
            
        </div><!--banner-->
    </div><!--bannerWrap--> 

	<div id="contentWrap">
    	<div id="contentHome">
        	<h2><span>Porque utilizar o VRaptor:</span></h2>

			<div id="boxvideo">
				<object width="400" height="300">
                	<param name="allowfullscreen" value="true" />    
                	<param name="allowscriptaccess" value="always" />
                	<param	name="movie" value="http://vimeo.com/moogaloop.swf?clip_id=5961030&amp;server=vimeo.com&amp;show_title=1&amp;show_byline=1&amp;show_portrait=0&amp;color=&amp;fullscreen=1"/>
                	<embed src="http://vimeo.com/moogaloop.swf?clip_id=5961030&amp;server=vimeo.com&amp;show_title=1&amp;show_byline=1&amp;show_portrait=0&amp;color=&amp;fullscreen=1" type="application/x-shockwave-flash" allowfullscreen="true" allowscriptaccess="always" width="400" height="300"></embed>
                </object>
			</div><!-- box video -->
            
            <div id="quickFeatures">
            
            	<div class="featuresBox grayBarHome">
                	<a id="produtividadeFB" href="beneficios.html">
                		<h3>alta produtividade</h3>
                    	<p>Se você precisa usar um controlador e sua equipe deve produzir código com boa qualidade, o VRaptor é uma das suas opções.Se você precisa usar um controlador e sua equipe deve produzir logo código com boa qualidade.</p>
                	</a>
                </div><!-- features box-->
                
            	<div class="featuresBox grayBarHome">
                	<a id="aprendizadoFB" href="beneficios.html">
                		<h3>curva de aprendizado</h3>
                    	<p>Se você precisa usar um controlador e sua equipe deve produzir código com boa qualidade, o VRaptor é uma das suas opções.Se você precisa usar um controlador e sua equipe deve produzir logo código com boa qualidade.</p>
                	</a>
                </div><!-- features box--> 
                
            	<div class="featuresBox grayBarHome">
                	<a id="testabilidadeFB" href="beneficios.html">
                		<h3>Testabilidade</h3>
                    	<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris rutrum ultricies Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris rutrum ultricies.</p>
                	</a>
                </div><!-- features box-->
                
             	<div class="featuresBox grayBarHome">
                	<a id="economiaFB" href="beneficios.html">
                		<h3>Economia</h3>
                    	<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris rutrum ultricies Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris rutrum ultricies.</p>
                	</a>
                </div><!-- features box-->                                               
            
            </div><!--quickFeatures-->
            
            <div id="twitterBox">
            	<h3><a href="http://twitter.com/vraptor3" target="_blank">Siga-nos no Twitter</a></h3>
                <div id="tweets">
                	<p>carregando tweets...</p>                    
                </div>
            </div><!-- twitterBox-->

        </div><!-- content cnt -->
    </div><!-- content wrap-->
<script src="http://twitterjs.googlecode.com/svn/trunk/src/twitter.min.js" type="text/javascript"></script>
<script type="text/javascript">
  //<![CDATA[
  getTwitters('tweets', { 
	  id: 'vraptor3', 
	  count: 2, 
	  enableLinks: true, 
	  ignoreReplies: true, 
	  clearContents: true,
	  template: '<p>%text%<br/><span><a href="http://twitter.com/%user_screen_name%/statuses/%id%/">%time%</a></span></p>'
	});
  //]]>
</script>  
<%@include file="/footer.jsp" %>