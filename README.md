# PolyMovie 
###  Microservices approach for building a movie polystore

 ![](http://i.imgur.com/agCbOoz.png)


### Overview
This repo contains a universitary project of Big Data course. The goal of this project is build a Movie Polystore in a microservices architecture using [TMDB](https://www.themoviedb.org/) as data source. Moreover this project includes a simple web application that presents Polystore main entities using Spring framework.


### Project Structure
* **Java Project** : this repository
* **Docker Polystore**: [repository](https://drive.google.com/drive/folders/0ByoFjS7ukGTkRTNQaXlXNWRrZ00?usp=sharing)


### Dependecies
* **Docker**
* **Tomcat**

### How to use
* Install docker and Tomcat
* Download the [Polystore](https://drive.google.com/drive/folders/0ByoFjS7ukGTkRTNQaXlXNWRrZ00?usp=sharing) and read the readme file inside to launch it
* After launched the Polystore with "docker-compose up -d" you can access to each db in localhost with the default port
* If you want to access to the Polystore with our Web App copy the PolyMovie.war file inside webapps folder in Tomcat 
* Launch Tomcat, verify it load the .war file,and then go to localhost:8080/Polymovie/


## Authors

* Alessandro Sgaraglia: [GitHub: AlexSgar]
* Alessandro Oddi: [GitHub: [adixia](https://github.com/adixia)]
