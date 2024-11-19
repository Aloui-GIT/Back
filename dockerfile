# Étape 1 : Utilisation de l'image officielle JDK 17
FROM eclipse-temurin:17-jdk-alpine

# Étape 2 : Définir le répertoire de travail à l'intérieur du conteneur
WORKDIR /app

# Étape 3 : Copier le fichier JAR généré vers le conteneur
COPY target/GenerateurForlulaire-0.0.1-SNAPSHOT.jar app.jar

# Étape 4 : Exposer un port (modifiez selon votre configuration)
EXPOSE 8082

# Étape 5 : Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]

