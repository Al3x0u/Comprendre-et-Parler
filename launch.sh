#!/bin/bash

PROPS="src/main/resources/application.properties"

current_login=$(grep "^db\.login=" "$PROPS" | sed 's/^db\.login=//' | tr -d '\r')
current_password=$(grep "^db\.password=" "$PROPS" | sed 's/^db\.password=//' | tr -d '\r')

if [ "$1" = "test" ]; then
    # Mode test
    if [ "$current_login" = "BD25SILENTCODE" ]; then
        echo "ERREUR : Les credentials du projet sont détectés. Entrez vos credentials personnels :"
        read -p "Nom d'utilisateur BD : " db_user
        read -s -p "Mot de passe BD : " db_pass
        echo
    elif [ -z "$current_login" ] || [ -z "$current_password" ]; then
        echo "Aucun credential trouvé. Entrez vos credentials :"
        read -p "Nom d'utilisateur BD : " db_user
        read -s -p "Mot de passe BD : " db_pass
        echo
    else
        echo "Credentials existants utilisés : $current_login"
        db_user=$current_login
        db_pass=$current_password
    fi

    sed -i "s/^db\.login=.*/db.login=$db_user/" "$PROPS"
    sed -i "s/^db\.password=.*/db.password=$db_pass/" "$PROPS"

    echo "Lancement des tests..."
    mvn clean test -e
    git restore "$PROPS"

else
    # Mode application
    if [ -z "$current_login" ] || [ -z "$current_password" ]; then
        echo "Aucun credential trouvé. Entrez vos credentials :"
        read -p "Nom d'utilisateur BD : " db_user
        read -s -p "Mot de passe BD : " db_pass
        echo

        sed -i "s/^db\.login=.*/db.login=$db_user/" "$PROPS"
        sed -i "s/^db\.password=.*/db.password=$db_pass/" "$PROPS"
    else
        echo "Credentials existants utilisés : $current_login"
    fi

    echo "Lancement de l'application..."
    mvn spring-boot:run
    git restore "$PROPS"
fi