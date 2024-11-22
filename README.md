# link-voyager

* The link voyager project
* Lenguaje: Java (7)
* Build System: Maven

## Shallow description

* It is a web app that comes with a friendly front used for explore given url.
* It is programmed to look for `<a>` tags and building a site map from those url, you may configure:
  * Number of workers: each worker recieves an url as parameter an does this => fetch the url, then parse it, the analize `<a>` tags and return next urls to scan (if are any)
  * Number of deep: How many pages do you wihs to get far from the source url. 
* And the back comes with an internal API for friendly usage!

## Disclaimerships

For reasons I never updated it, it only works to explore sites on servers that do not have ssl certificates, ergo, only http protocol (without s)
  
This is "paperwork" that can be corrected with a little time...
  
## stream of consciousness

The motivation arises after reading half of the book "Concurrency in Practice" Brian Goetz (friend of the kapo Joshua Block) and wanted to put into trial if concurrency actually served to explore the web faster, creating threads where each one explores an assigned page and coordinating the work so that the same page is not explored twice.
  
Bottom line is: Concurrency DOES improve web. Thanks for your honorships.

I went a little mambo with the abstractions and the code because I was younger and had more desire to delirious with eternal designs.

