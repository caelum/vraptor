<%@include file="/header.jsp" %>
<div id="bannerWrap">
    	<div id="banner">
            
            <div id="floatBoxBanner">
            	<img src="${path }/images/iconOpenSource-trans.png" id="openSourceIcon" />
	            <h1><fmt:message key="experimente.vraptor"/></h1>
	            <ul>
	            	<li><fmt:message key="experimente.vraptor.motivo1"/></li>
	            	<li><fmt:message key="experimente.vraptor.motivo2"/></li>
	            	<li><fmt:message key="experimente.vraptor.motivo3"/></li>
    	        </ul>
       	     	<a href="download.jsp" id="downloadBtn2"><span>Download</span></a>
                <p><a href="<fmt:message key="saiba.mais.link"/>"><fmt:message key="saiba.mais"/></a> </p>
			</div><!-- content-float -->
            <img src="${path }/images/boxVraptor-trans.png" />
            
        </div><!--banner-->
    </div><!--bannerWrap--> 

	<div id="contentWrap">
    	<div id="contentHome">
        	<h2><span><fmt:message key="porque.utilizar"/>:</span></h2>

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
                	<a id="produtividadeFB" href="${path }/<fmt:message key="beneficios.link"/>">
                		<h3><fmt:message key="alta.produtividade"/></h3>
                    	<p><fmt:message key="alta.produtividade.text"/></p>
                	</a>
                </div><!-- features box-->
                
            	<div class="featuresBox grayBarHome">
                	<a id="aprendizadoFB" href="${path }/<fmt:message key="beneficios.link"/>">
                		<h3><fmt:message key="curva.de.aprendizado"/></h3>
                    	<p><fmt:message key="curva.de.aprendizado.text"/></p>
                	</a>
                </div><!-- features box--> 
                
            	<div class="featuresBox grayBarHome">
                	<a id="testabilidadeFB" href="${path }/<fmt:message key="beneficios.link"/>">
                		<h3><fmt:message key="testabilidade"/></h3>
                    	<p><fmt:message key="testabilidade.text"/></p>
                	</a>
                </div><!-- features box-->
                
             	<div class="featuresBox grayBarHome">
                	<a id="economiaFB" href="${path }/<fmt:message key="beneficios.link"/>">
                		<h3><fmt:message key="economia"/></h3>
                    	<p><fmt:message key="economia.text"/></p>
                	</a>
                </div><!-- features box-->                                               
            
            </div><!--quickFeatures-->
            
            <div id="twitterBox">
            	<h3><a href="http://twitter.com/vraptor3" target="_blank"><fmt:message key="siga.nos"/></a></h3>
                <div id="tweets">
                	<p><fmt:message key="carregando.tweets"/></p>
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