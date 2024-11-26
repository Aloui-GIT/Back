FROM openjdk:17
COPY target/GenerateurForlulaire-0.0.1.jar  .
EXPOSE 8082
ENTRYPOINT ["java","-jar","GenerateurForlulaire-0.0.1.jar"]
