FROM java:8 
EXPOSE 8080  
ADD target/VXAdminWebCrawlV1.0-1.0-SNAPSHOT.jar VXAdminWebCrawlV1.0-1.0-SNAPSHOT.jar 
ENTRYPOINT ["java","-jar","VXAdminWebCrawlV1.0-1.0-SNAPSHOT.jar"]
