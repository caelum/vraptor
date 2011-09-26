%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%
%%                  Caelum ObjectsTubaina
%%

\documentclass[a4paper, 11pt, oneside]{book}

%% For portuguese compatibility
\usepackage[brazil]{babel}
\usepackage[latin1]{inputenc}
\usepackage[T1]{fontenc}
\usepackage{float}
\usepackage[scaled]{helvet}
\renewcommand*\familydefault{\sfdefault}
\usepackage{indentfirst}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Packages

\usepackage[dvips]{graphicx,psfrag}
\usepackage{ifthen}
\usepackage{makeidx}
\usepackage{enumerate}
\usepackage{fancyhdr}
\usepackage{mintedx}
\usepackage{pstcol}
\usepackage{colortbl}
\usepackage{url}
\usepackage{rotating}
\usepackage{multirow}
\usepackage{colortbl}
\usepackage[table]{xcolor}

\usepackage{tubaina}

%This has to be the last package declared, for some reason
\usepackage[pdftex]{hyperref}

\definecolor{boxhead}{gray}{0.8}

%%%%%%%PAGE SETUP%%%%%%%%%%%%%
\oddsidemargin    -6mm
\textwidth        180mm
\topmargin        -10mm
\textheight       250mm
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\makeatletter
\makeatother
\setcounter{tocdepth}{2}

\makeindex

\begin{document}
\usemintedstyle{eclipse}

\normalfont

\thispagestyle{empty}

<#include "cover.ftl">
\newpage

\newpage
\pagestyle{plain}
\pagenumbering{roman}
\setcounter{page}{1} 

\tableofcontents

\centerline{\bf Version: ${textbookVersion}}

\newpage

\newsavebox{\logotubaina}
\sbox{\logotubaina}{\includegraphics[totalheight=1em]{tubaina.png}}
\pagestyle{fancy}
\fancyhf{}

\lhead{\footnotesize{\usebox{\logotubaina}\hspace{2mm} Caelum -- ${book.name}}}

%\rhead{\nouppercase{\bfseries\rightmark}}
%\lhead{\nouppercase{\bfseries\leftmark}}
\makeatletter
\renewcommand{\chaptermark}[1]{\markboth{\@chapapp\ \thechapter\ -\ #1}{}}
\makeatother
\renewcommand{\sectionmark}[1]{\markright{\ #1}{}}
\rfoot{\footnotesize\nouppercase{\leftmark\ - \rightmark\ -- \thepage}}



\pagenumbering{arabic}
\setcounter{page}{1}
