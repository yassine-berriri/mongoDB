// Set MYPATH=D:\tpmongodb2021
// javac -g -cp %MYPATH%\mongojar\mongo-java-driver-3.12.7.jar;%MYPATH% %MYPATH%\tpjava\Employe.java
// java -Xmx256m -Xms256m -cp %MYPATH%\mongojar\mongo-java-driver-3.12.7.jar;%MYPATH% tpjava.Employe

/**
Le package TP contient deux classes. La classe Employe et la classe DeptEmp
Chacune de ces classes est dans son propre fichier.
La classe Employe contient les methodes suivantes :

	public void insertOneEmploye(String nomCollection, Document employe);
	public void testInsertOneEmploye();
	public void insertManyEmployes(String nomCollection, List<Document> employes);
	public void testInsertManyEmployes();
	public void getEmployeById(String nomCollection, Integer empId);
	public void getEmployes(String nomCollection, 
		Document whereQuery, 
		Document projectionFields,
		Document sortFields);
	public void updateEmployes(String nomCollection, 
		Document whereQuery, 
		Document updateExpressions, UpdateOptions updateOptions);
	public void deleteEmployes(String nomCollection, Document filters);
	public void displayIterator(Iterator it, String message);   
	public void joinLocalAndforeignCollections(
				String localCollectionName, 
				String foreignCollectionName, 
				String localColJoinFieldName,
				String foreigColJoinFieldName,
				Document fielterFieldsOnLocalCollection,
				String namedJoinedElements
	);
	public void groupBy(
            String collectionName,
			String groupOperator,
			Document groupFields) ;
	public void createEmployeIndexes(
		String localCollectionName,
		String indexName,
		String indexFieldName1,
		String indexFieldName2,
		String indexFieldName3,
		String indexType,
		boolean isAscendingIndex,
		boolean indexUnique);		
	public void getAllIndexesOfACollection(String localCollectionName);
	public void dropAIndexOfACollection(
		String localCollectionName,
		String indexName);

Chaque methode est decrite lors de sa declaration dans la classe.

*/

package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase; 
import com.mongodb.client.*;
import com.mongodb.MongoCredential; 
import com.mongodb.DBObject;  
import com.mongodb.BasicDBObject; 
/*import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DB; */
import com.mongodb.client.model.*;
import org.bson.Document;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import com.mongodb.client.FindIterable; 
import com.mongodb.client.ListIndexesIterable;
import java.util.Iterator; 
import java.util.ArrayList;
import java.util.logging.Filter;

import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.AggregateIterable;
import org.bson.conversions.Bson;

public class Employe { 
   private MongoDatabase database;
   private String dbName="RH";
   private String hostName="localhost";
   private int port=27017;
   private String userName="urh";
   private String passWord="passUrh";
   private String EmployeCollectionName="colEmployes";

   /**
   Le  
	
   */
   public static void main( String args[] ) {  
    try{
		Employe emp = new Employe();
		// TE1a : test des fonctions de gestion d'une collection et d'ajout de documents

		emp.dropCollectionEmploye(emp.EmployeCollectionName);
		emp.createCollectionEmploye(emp.EmployeCollectionName);
		emp.deleteEmployes(emp.EmployeCollectionName, new Document());
		//emp.testInsertOneEmploye();
		emp.testInsertManyEmployes();
		emp.getEmployeById(emp.EmployeCollectionName, 7934);

		// TE1b: Afficher tous les employes sans tri ni projection
		System.out.println("\n\nTE1b : ...");

		emp.getEmployes(emp.EmployeCollectionName, 
			new Document(), 
			new Document(),
			new Document());
		
		// TE2 : Afficher tous les employes salesman du departement 30
		// Trie en ordre croissant sur _id
		// Projete sur _id, ename, job, deptno et adresse
		System.out.println("\n\nTE2 : ...");

		emp.getEmployes(emp.EmployeCollectionName, 
			new Document("job", "Salesman").append("deptno",30), 
			new Document("_id", 1).append("ename",1).
			append("job",1).append("deptno", 1).
			append("adresse",1),
			new Document("_id", 1).append("ename",1)
		);
		
		// TE3 : Augmenter les employes travaillant dans le departement de 	10%
		
		System.out.println("\n\nTE3 : ...");

		emp.updateEmployes(emp.EmployeCollectionName, 
		new Document("deptno", 30), 
		new Document ("$mul", new Document("sal", 1.1) ),
		new UpdateOptions());
		
		// TE4 : afficher les informations sur les employes du departement 10
		
		
		System.out.println("\n\nTE4 : ...");

		emp.joinLocalAndforeignCollections("colDepts","colEmployes", "_id", "deptno", 
			new Document("_id", 10), "EmployesQuiMatchent");

		// TE5b: afficher les employes groupes par job
		System.out.println("\n\nTE5b : ...");
/*
		emp.groupBy("colEmployes", "$group", new Document("_id", "$job").
				append("masseSalaire", new Document("$sum",  "$sal")).
				append("moySalaire", new Document("$avg",  "$sal")).
				append("countSalaire", new Document("$count",  new Document())));
*/
		// TE6: Creer un index nomme
		// Creation d'un index
		System.out.println("\n\nTE6 : ...");

		emp.createEmployeIndexes(
			emp.EmployeCollectionName,
			"idx_"+emp.EmployeCollectionName+"_ename",
			new ArrayList<String> ( Arrays.asList("ename")),
			true,
			false
		);
		// TE7 : recuperer les informations sur tous les indexes
		System.out.println("\n\nTE7 : ...");

		emp.getAllIndexesOfACollection(emp.EmployeCollectionName);

		// TE8 : supprimer un index
		System.out.println("\n\nTE8 : ...");

		emp.dropAIndexOfACollection(emp.EmployeCollectionName, "idx_colEmployes_ename");

	}catch(Exception e){
		e.printStackTrace();
	}	
   } 
   
   /**
	FE1 : Constructeur Employe.
	Dans ce constructeur sont effectuees les activites suivantes:
	- Creation d'une instance du client MongoClient
	- Creation d'une BD Mongo appele RH
	- Creation d'un utilisateur appele 
	- Chargement du pointeur vers la base RH
   */
   
   Employe(){
		// Creating a Mongo client
		
		MongoClient mongoClient = MongoClients.create();

		// Creating Credentials 
		// RH : Ressources Humaines
		MongoCredential credential; 
		credential = MongoCredential.createCredential(userName, dbName, 
		 passWord.toCharArray()); 
		System.out.println("Connected to the database successfully"); 	  
		System.out.println("Credentials ::"+ credential);  
		// Accessing the database 
		database = mongoClient.getDatabase(dbName); 

   }

   /**
	FE2 : Cette fonction permet de cr�er une collection
	de nom nomCollection.
	Travail � faire : compl�ter cette m�thode
   */
   public void createCollectionEmploye(String nomCollection){
	//Creating a collection 
	// A compl�ter !!!!!!!!!
	   database.createCollection(nomCollection);
	   	System.out.println("Collection Employes created successfully");

   }
   
   
   /**
	FE3 : Cette fonction permet de supprimer une collection
	de nom nomCollection.
		Travail � faire : compl�ter cette m�thode

   */
   
   public void dropCollectionEmploye(String nomCollection){
		//Drop a collection 
		MongoCollection<Document> colEmployes=null;
	    System.out.println("\n\n\n*********** dans dropCollectionDept *****************");
	   // A compl�ter !!!!!!!!!!!!

	   System.out.println("!!!! Collection Employe : "+colEmployes);

	   colEmployes= database.getCollection(nomCollection);

	   if (colEmployes==null)
		   System.out.println("Collection inexistante");
	   else {
		   colEmployes.drop();
		   System.out.println("Collection colEmployes removed successfully !!!");
	   }


   }

   /**
	FE4 : Cette fonction permet d'ins�rer un employ� dans une collection.
	Travail � faire : compl�ter cette m�thode

   */
   
   public void insertOneEmploye(String nomCollection, Document employe){
		//Drop a collection 
		// A compl�ter !!!!!!!!!!!!

	   // accéder à la collection spécifié
			MongoCollection<Document> collection = database.getCollection(nomCollection);
			//insérer l'employé dans la collection
			collection.insertOne(employe);
		System.out.println("Document inserted successfully");
   }

   /**
	FE5 : Cette fonction permet de tester la m�thode insertOneEmploye.
	Travail � faire : compl�ter cette m�thode

   */

   public void testInsertOneEmploye(){
	// A compl�ter !!!!!!!!!
	   Document employe = new Document("_id", 7369)
			   .append("ename", "SMITH")
			   .append("job","Clerk");
	   this.insertOneEmploye(this.EmployeCollectionName, employe);
		System.out.println("Document inserted successfully");     
   }

   /**
	FE6 : Cette fonction permet d'ins�rer plusieurs Employ�s dans une collection
	Travail � faire : compl�ter cette m�thode

	*/

   public void insertManyEmployes(String nomCollection, List<Document> employes){
	// A compl�ter !!!!!!!!!
	   MongoCollection<Document> colEmployes=database.getCollection(nomCollection);
	   colEmployes.insertMany(employes);
	   System.out.println("Many Documents inserted successfully");
   }

   /**
	FE7 : Cette fonction permet de tester la fonction insertManyEmployes
   */

   public void testInsertManyEmployes(){
		List<Document> employes = Arrays.asList(
		new Document("_id", 7839) 
		.append("ename", "KING")
		.append("prenoms", Arrays.asList("Leroi", "Mokondji"))
		.append("adresse", new Document(
			"numero", 6)
			.append("rue", "Traverse en Escalier")
            .append("ville", "Valbonne")
            .append("codePostal", "06560")
            .append("Pays", "France"))
		.append("job", "Pr�sident") 
		.append("mgr", null) 
		.append("hiredate", "17-11-1981") 
		.append("sal", 5000)
		.append("comm", null)
		.append("deptno", 10),
		
		
		new Document(
		"_id", 7369) 
		.append("ename", "SMITH")
		.append("prenoms", Arrays.asList("Will", "Williamson"))
		.append("adresse", new Document(
			"numero", 11)
			.append("rue", "Du capitol")
            .append("ville", "Toulouse")
            .append("codePostal", "31000")
            .append("Pays", "France"))
		.append("job", "Clerk") 
		.append("mgr", 7902) 
		.append("hiredate", "17-12-1980") 
		.append("sal", 800)
		.append("comm", null)
		.append("deptno", 20),
		
		new Document(
		"_id", 7499) 
		.append("ename", "ALLEN")
		.append("prenoms", Arrays.asList("Isaora", "Maria"))
		.append("adresse", new Document(
			"numero", 11)
			.append("rue", "Des Mimosas")
            .append("ville", "Toulon")
            .append("codePostal", "83000")
            .append("Pays", "France"))
		.append("job", "Salesman") 
		.append("mgr", 7698) 
		.append("hiredate", "17-02-1981") 
		.append("sal", 1600)
		.append("comm", 300)
		.append("deptno", 30),


		new Document(
		"_id", 7521) 
		.append("ename", "WARD")
		.append("prenoms", Arrays.asList("Julian", "Emery", "Andy"))
		.append("adresse", new Document(
			"numero", 11)
			.append("rue", "Place Garibaldi")
            .append("ville", "Nice")
            .append("codePostal", "06000")
            .append("Pays", "France"))
		.append("job", "Salesman") 
		.append("mgr", 7698) 
		.append("hiredate", "22-FEB-1981") 
		.append("sal", 1250)
		.append("comm", 500)
		.append("deptno", 30),


		new Document(
		"_id", 7566) 
		.append("ename", "JONES")
		.append("prenoms", Arrays.asList("Julian"))
		.append("adresse", new Document(
			"numero", 11)
			.append("rue", "Pertinax")
            .append("ville", "Nice")
            .append("codePostal", "08000")
            .append("Pays", "France"))
		.append("job", "Manager") 
		.append("mgr", 7839) 
		.append("hiredate", "1-04-1981") 
		.append("sal", 2975)
		.append("comm", null)
		.append("deptno", 20),

		
		new Document(
		"_id", 7654) 
		.append("ename", "MARTIN")
		.append("prenoms", Arrays.asList("Le pecheur"))
		.append("adresse", new Document(
			"numero", 10)
			.append("rue", "Canebiere")
            .append("ville", "Marseille")
            .append("codePostal", "13000")
            .append("Pays", "France"))
		.append("job", "Salesman") 
		.append("mgr", 7698) 
		.append("hiredate", "28-11-1981") 
		.append("sal", 1250)
		.append("comm", 1400)
		.append("deptno", 30),


		new Document(
		"_id", 7698) 
		.append("ename", "BLAKE")
		.append("prenoms", Arrays.asList("Lenoir"))
		.append("adresse", new Document(
			"numero", 11)
			.append("rue", "Rond point des Africains")
            .append("ville", "Cogolin")
            .append("codePostal", "83310")
            .append("Pays", "France"))
		.append("job", "Manager") 
		.append("mgr", 7839) 
		.append("hiredate", "1-05-1981") 
		.append("sal", 2850)
		.append("comm", null)
		.append("deptno", 30),


		new Document(
		"_id", 7782) 
		.append("ename", "CLARK")
		.append("prenoms", Arrays.asList("Jonson"))
		.append("adresse", new Document(
			"numero", 27)
			.append("rue", "Des Miracles")
            .append("ville", "Toulouse")
            .append("codePostal", "31000")
            .append("Pays", "France"))
		.append("job", "Manager") 
		.append("mgr", 7839) 
		.append("hiredate", "9-06-1981") 
		.append("sal", 2450)
		.append("comm", null)
		.append("deptno", 10),


		new Document(
		"_id", 7788) 
		.append("ename", "SCOTT")
		.append("prenoms", Arrays.asList("Tiger"))
		.append("adresse", new Document(
			"numero", 75)
			.append("rue", "Jean Jaures")
            .append("ville", "Nice")
            .append("codePostal", "06000")
            .append("Pays", "France"))
		.append("job", "Analyst") 
		.append("mgr", 7566) 
		.append("hiredate", "09-12-1982") 
		.append("sal", 3000)
		.append("comm", null)
		.append("deptno", 20),

		
		new Document(
		"_id", 7844) 
		.append("ename", "TURNER")
		.append("prenoms", Arrays.asList("Allan", "Johnson"))
		.append("adresse", new Document(
			"numero", 11)
			.append("rue", "d'Italie")
            .append("ville", "Nice")
            .append("codePostal", "06000")
            .append("Pays", "France"))
		.append("job", "Salesman") 
		.append("mgr", 7698) 
		.append("hiredate", "8-09-1981") 
		.append("sal", 1500)
		.append("comm", 0)
		.append("deptno", 30),

		
		new Document(
		"_id", 7876) 
		.append("ename", "ADAMS")
		.append("prenoms", Arrays.asList("Le premier"))
		.append("adresse", new Document(
			"numero", 378)
			.append("rue", "Rue du Paradis")
            .append("ville", "Marseille")
            .append("codePostal", "13000")
            .append("Pays", "France"))
		.append("job", "Clerk") 
		.append("mgr", 7788) 
		.append("hiredate", "12-01-1983") 
		.append("sal", 1100)
		.append("comm", null)
		.append("deptno", 20),
		
		new Document(
		"_id", 7900) 
		.append("ename", "JAMES")
		.append("prenoms", Arrays.asList("Bond"))
		.append("adresse", new BasicDBObject(
			"numero", 12)
			.append("rue", "Rue d'Angleterre")
            .append("ville", "Nice")
            .append("codePostal", "06000")
            .append("Pays", "France"))
		.append("job", "Clerk") 
		.append("mgr", 7698) 
		.append("hiredate", "9-12-1981") 
		.append("sal", 950)
		.append("comm", null)
		.append("deptno", 30),
		
		new Document(
		"_id", 7902) 
		.append("ename", "FORD")
		.append("prenoms", Arrays.asList("James"))
		.append("adresse", new BasicDBObject(
			"numero", 11)
			.append("rue", "Des voitures")
            .append("ville", "Toulouse")
            .append("codePostal", "31000")
            .append("Pays", "France"))
		.append("job", "Analyst") 
		.append("mgr", 7566) 
		.append("hiredate", "3-12-1981") 
		.append("sal", 3000)
		.append("comm", null)
		.append("deptno", 20),

		new Document(
		"_id", 7934) 
		.append("ename", "MILLER")
		.append("prenoms", Arrays.asList("Rosa", "Alison"))
		.append("adresse", new BasicDBObject(
			"numero", 11)
			.append("rue", "Place des �toiles")
            .append("ville", "Biarritz")
            .append("codePostal", "64200")
            .append("Pays", "France"))
		.append("job", "Clerk") 
		.append("mgr", 7782) 
		.append("hiredate", "23-01-1982") 
		.append("sal", 1300)
		.append("comm", null)
		.append("deptno", 10)
		
		);  
		this.insertManyEmployes(this.EmployeCollectionName, employes);
   }

   /**
	FE8 : Cette fonction permet de rechercher un employ� dans une collection
	connaissant son id.
	Travail � faire : compl�ter cette m�thode

   */

   public void getEmployeById(String nomCollection, Integer empId){
	// A compl�ter !!!!!!!!!
	   System.out.println("\n\n\n*********** dans getEmployeById *****************");

	   MongoCollection<Document> colEmployes=database.getCollection(nomCollection);

	   //BasicDBObject whereQuery = new BasicDBObject();
	   Document whereQuery = new Document();

	   whereQuery.put("_id", empId);
	   //DBCursor cursor = colDepts.find(whereQuery);
	   FindIterable<Document> listDept=colEmployes.find(whereQuery);

	   // Getting the iterator
	   Iterator it = listDept.iterator();
	   while(it.hasNext()) {
		   System.out.println(it.next());
	   }
   }

   
   
    /**
	FE9 :Cette fonction permet de rechercher des employ�s dans une collection.
	Le parametre whereQuery : permet de passer des conditions de rechercher
	Le parametre projectionFields : permet d'indiquer les champs a afficher
	Le parametre sortFields : permet d'indiquer les champs de tri.
	
	Travail � faire : compl�ter cette m�thode

   */

   public void getEmployes(String nomCollection, 
   	Document whereQuery, 
	Document projectionFields,
	Document sortFields){
		// A compl�ter !!!!!!!!!!
	   System.out.println("\n\n\n*********** dans getDepts *****************");

	   MongoCollection<Document> colEmploye=database.getCollection(nomCollection);
	   FindIterable<Document> listEmp=colEmploye.find(whereQuery).sort(sortFields).projection(projectionFields);

	   Iterator it = listEmp.iterator();
	   while (it.hasNext()) {
		   System.out.println(it.next());
	   }
   }

    /**
	FE10 :Cette fonction permet de modifier des employ�s dans une collection.
	Le parametre whereQuery : permet de passer des conditions de recherche
	Le parametre updateExpressions : permet d'indiquer les champs a modifier
	Le parametre UpdateOptions : permet d'indiquer les options de mise a jour :
		.upSert : insere si le document n'existe pas
	
	Travail � faire : compl�ter cette m�thode

   */

   public void updateEmployes(String nomCollection, 
   	Document whereQuery, 
	Document updateExpressions, UpdateOptions updateOptions){
	// A compl�ter !!!!!
	   System.out.println("\n\n\n*********** dans updateEmp *****************");

	   MongoCollection<Document> colEmployes=database.getCollection(nomCollection);
	   UpdateResult updateResult = colEmployes.updateMany(whereQuery, updateExpressions);

	   System.out.println("\nR�sultat update : "
			   +"getUpdate id: "+updateResult
			   +" getMatchedCount : "+updateResult.getMatchedCount()
			   +" getModifiedCount : "+updateResult.getModifiedCount()
	   );
   }


    /**
	FE11 :Cette fonction permet de supprimer des employ�s dans une collection.
	Le parametre filters : permet de passer des conditions de recherche des employ�s a supprimer
	
	Travail � faire : compl�ter cette m�thode

 */
   public void deleteEmployes(String nomCollection, Document filters){
		
		System.out.println("\n\n\n*********** dans deleteEmployes *****************");   
		// A compl�ter !!!!!!!!!!!!
	   FindIterable<Document> listEmployes;
	   Iterator it;
	   MongoCollection<Document> colEmployee=database.getCollection(nomCollection);

	   listEmployes=colEmployee.find(filters).sort(new Document("_id", 1));
	   it = listEmployes.iterator();
	   this.displayIterator(it, "Dans deleteEmployes: avant suppression");
	   colEmployee.deleteMany(filters);
	   listEmployes=colEmployee.find(filters).sort(new Document("_id", 1));
	   it = listEmployes.iterator();
	   this.displayIterator(it, "Dans deleteEmployes: apres suppression");
   } 	
   
   
   /**
	FE12 :Parcours un it�rateur et affiche les documents qui s'y trouvent
   */
   public void displayIterator(Iterator it, String message){
	System.out.println(" \n #### "+ message + " ################################");
	while(it.hasNext()) {
		System.out.println(it.next());
		
	}		
	
	
   }


/**
	FE13 : Cette fonction permet d'effectuer une jointure entre les collections dept et employe
	Elle permet d'afficher les employes par departement.
	Le parametre localCollectionName : nom de la 1ere collection a joindre
	Le parametre ForeignCollectionName : nom de la 2eme collection a joindre
	Le parametre localColJoinFieldName : champ de jointure de la 1 ere collection
	Le parametre foreigColJoinFieldName :  champ de jointure de la 2 eme collection
	Le parametre fielterFieldsOnLocalCollection : Champ de restriction
	Le parametre namedJoinedElements : Nom des �l�ments de la foreignCollectionName qui matchent
	
	Travail � faire : completer cette methode
   */

	public void joinLocalAndforeignCollections(
				String localCollectionName, 
				String foreignCollectionName, 
				String localColJoinFieldName,
				String foreigColJoinFieldName,
				Document fielterFieldsOnLocalCollection,
				String namedJoinedElements
	){

	// A compl�ter !!!!!!!!!!!!
		/***/
		AggregateIterable<Document> outPutColl;
		MongoCollection<Document> joinColl = database.getCollection(localCollectionName);

		System.out.println("\n\n\n ****** dans joinLocalAndforeignCollections********");
		outPutColl=joinColl.aggregate(Arrays.asList(
				Aggregates.match(fielterFieldsOnLocalCollection),
				Aggregates.lookup(foreignCollectionName, localColJoinFieldName, foreigColJoinFieldName, namedJoinedElements)

		));

		for(Document colDoc: outPutColl) {
			System.out.println(colDoc);
		}

	}   


   /**
	FE14 : Cette fonction permet d'effectuer des operations de groupes
	dans la collection de nom collection
	Le param�tre groupOperator : L'operateur de groupe doit etre "$group"
	Le param�tre groupFields : documents contenant les champs de groupement, moyennes, sommes, etc.
	Travail � faire : completer cette methode

   */

	public void groupBy(
            String collectionName,
			String groupOperator,
			Document groupFields) {

 	// A compl�ter !!!!!!!!!!!!
		System.out.println("\n\n\n*********** dans groupBy *****************");

		MongoCollection<Document> colEmployes = database.getCollection(collectionName);
		Document group = new Document(groupOperator, groupFields);

		AggregateIterable<Document> output =
				colEmployes.aggregate(Arrays.asList(group));
		for(Document document:output) {
			System.out.println(document);
		}

    }
	

    /**
	FE15 : Cette fonction permet de creer un index dans la collection.
		- Le parametre localCollectionName : Nom de la collection
		- Le parametre indexName : Nom de l'indexe
		- Le parametre fieldNames : Nom des champs � indexer
		- Le parametre isAscendingIndex : true si ascendant, false sinon
		- Le parametre indexUnique : true si index unique false sinon
		
		Travail � faire : completer cette methode

	
   */
	public void createEmployeIndexes(
		String localCollectionName,
		String indexName,
		List<String> fieldNames,
		boolean isAscendingIndex,
		boolean indexUnique
	){
		
		
		System.out.println("\n\n\n*********** dans createEmployeIndexes *****************");   
		String returnedIndexName;
		
		MongoCollection<Document> colEmployes=database.getCollection(localCollectionName);
		IndexOptions indexOptions=new IndexOptions() ;
		if (indexName!= null)
			indexOptions.unique(indexUnique).name(indexName);
		else
			indexOptions.unique(indexUnique);
		
		// ascending(java.util.List<java.lang.String> fieldNames)
		if (isAscendingIndex==true)
			returnedIndexName=colEmployes.createIndex(Indexes.ascending(fieldNames), indexOptions);
		else
			returnedIndexName=colEmployes.createIndex(Indexes.descending(fieldNames), indexOptions);
		System.out.println("\n\nNom de l'index cr�e : "+
			returnedIndexName);   
		
	}

    /**
	FE16 : Cette fonction affiche les informations sur tous les indexes d'une collection.
	
	Travail � faire : completer cette methode

   */
	public void getAllIndexesOfACollection(
		String localCollectionName
	){
		
		
		System.out.println("\n\n\n*********** dans getAllIndexesOfACollection *****************"); 
		MongoCollection<Document> colEmployes=database.getCollection(localCollectionName);		
		ListIndexesIterable<Document>	liIndexIter=colEmployes.listIndexes();
		Iterator it = liIndexIter.iterator();// Getting the iterator
		this.displayIterator(it, "Dans getAllIndexesOfACollection:  Liste des indexes de la collection "+localCollectionName);
		
	}


   /**
	FE17 :Cette fonction supprime un index dans une collection connaissant son nom.
	
	Travail � faire : completer cette methode

   */
	public void dropAIndexOfACollection(
		String localCollectionName,
		String indexName
	){
		System.out.println("\n\n\n*********** dans dropAIndexOfACollection *****************");   
		MongoCollection<Document> colEmployes=database.getCollection(localCollectionName);	
		colEmployes.dropIndex(indexName);

	}

}
