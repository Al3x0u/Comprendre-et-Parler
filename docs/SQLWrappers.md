## Méthodes

Utiliser SQLWrap.callTransaction() pour englober plusieurs opérations dans une seule transaction SQL.

Utiliser SQLWrap.call() pour profiter de la même gestion d'erreur sans transaction SQL. 

## Limitations

Les méthodes ou lambdas passées en paramètre ne peuvent utiliser qu'au maximum trois arguments. Ces arguments doivent être déclarés dans la signature de la lambda, ainsi que passés en paramètres de la méthode call() (à la suite de la lambda).


## Exemple

```Java
DatabaseConnector.initialize();
        DAOStatus dao = new DAOStatus();
        Status stat1 = new Status("Premier statut de test", 5);
        Status stat2 = new Status("Deuxième statut de test", 3);
 
        SQLWrap.callTransaction((Status p1, Status p2) -> { // p1 et p2 paramètres formels de la lambda
            dao.create(p1);
            dao.create(p2);
        }, stat1, stat2); // stat1 et stat2 paramètres effectifs

        Set<Status> set = SQLWrap.call(dao::findAll);
        for (Status s : set) {
            System.out.println(s);
        }
        stat1.setHourQuota(7);
        SQLWrap.callTransaction(dao::update, stat1);
        SQLWrap.call((id) -> {
            Status updatedStatus = dao.find(id);
            System.out.println(updatedStatus);
        }, stat1.getId());  
```
