cd src/content
for i in `find -name *.html`; do rename 's/.html$/.jsp/' $i; done