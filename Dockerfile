FROM openjdk:17
COPY /var/lib/jenkins/workspace/qqq/Back/target/GenerateurForlulaire-0.0.1.jar .
EXPOSE 8082
ENTRYPOINT ["java","-jar","GenerateurForlulaire-0.0.1.jar"]
