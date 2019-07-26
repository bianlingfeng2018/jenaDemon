import org.junit.Test;
//import org.neo4j.graphdb.DynamicRelationshipType;
//import org.neo4j.graphdb.GraphDatabaseService;
//import org.neo4j.graphdb.Node;
//import org.neo4j.graphdb.Transaction;
//import org.neo4j.graphdb.factory.GraphDatabaseFactory;
//import org.neo4j.graphdb.index.UniqueFactory;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;


public class OWLAPITest {
//
//    @Test
//    public void OWL2Neo4jTest() throws Exception {
//        OWLOntology ontology = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(IRI.create(new File("/Users/bianlingfeng/IdeaProjects/jenaDemon/src/main/resources/data/pizza.owl.rdf")));
//        importOntology(ontology);
//    }
//
//    @Test
//    public void JavaNeo4jCQLRetrivalTest(){
//
//    }
//
//    private void importOntology(OWLOntology ontology) throws Exception {
//        Logger logger = LoggerFactory.getLogger(OWLAPITest.class);
//        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
//        GraphDatabaseService db = dbFactory.newEmbeddedDatabase(new File("/Users/bianlingfeng/IdeaProjects/jenaDemon/neo4j/embedded/data"));
//
//        OWLReasoner reasoner = new Reasoner(ontology);
//        if (!reasoner.isConsistent()) {
//            logger.error("Ontology is inconsistent");
//            //throw your exception of choice here
//            throw new Exception("Ontology is inconsistent");
//        }
//        Transaction tx = db.beginTx();
//        try {
//            Node thingNode = getOrCreateNodeWithUniqueFactory("owl:Thing", db);
//            for (OWLClass c : ontology.getClassesInSignature(true)) {
//                String classString = c.toString();
//                if (classString.contains("#")) {
//                    classString = classString.substring(
//                            classString.indexOf("#") + 1, classString.lastIndexOf(">"));
//                }
//                Node classNode = getOrCreateNodeWithUniqueFactory(classString, db);
//                NodeSet<OWLClass> superclasses = reasoner.getSuperClasses(c, true);
//                if (superclasses.isEmpty()) {
//                    classNode.createRelationshipTo(thingNode,
//                            DynamicRelationshipType.withName("isA"));
//                } else {
//                    for (org.semanticweb.owlapi.reasoner.Node<OWLClass>
//                            parentOWLNode : superclasses) {
//                        OWLClassExpression parent =
//                                parentOWLNode.getRepresentativeElement();
//                        String parentString = parent.toString();
//                        if (parentString.contains("#")) {
//                            parentString = parentString.substring(
//                                    parentString.indexOf("#") + 1,
//                                    parentString.lastIndexOf(">"));
//                        }
//                        Node parentNode =
//                                getOrCreateNodeWithUniqueFactory(parentString, db);
//                        classNode.createRelationshipTo(parentNode,
//                                DynamicRelationshipType.withName("isA"));
//                    }
//                }
//                for (org.semanticweb.owlapi.reasoner.Node<OWLNamedIndividual> in
//                        : reasoner.getInstances(c, true)) {
//                    OWLNamedIndividual i = in.getRepresentativeElement();
//                    String indString = i.toString();
//                    if (indString.contains("#")) {
//                        indString = indString.substring(
//                                indString.indexOf("#") + 1, indString.lastIndexOf(">"));
//                    }
//                    Node individualNode =
//                            getOrCreateNodeWithUniqueFactory(indString, db);
//                    individualNode.createRelationshipTo(classNode,
//                            DynamicRelationshipType.withName("isIndividualOf"));
//                    for (OWLObjectPropertyExpression objectProperty :
//                            ontology.getObjectPropertiesInSignature()) {
//                        for
//                        (org.semanticweb.owlapi.reasoner.Node<OWLNamedIndividual>
//                                object : reasoner.getObjectPropertyValues(i,
//                                objectProperty)) {
//                            String reltype = objectProperty.toString();
//                            reltype = reltype.substring(reltype.indexOf("#") + 1,
//                                    reltype.lastIndexOf(">"));
//                            String s =
//                                    object.getRepresentativeElement().toString();
//                            s = s.substring(s.indexOf("#") + 1,
//                                    s.lastIndexOf(">"));
//                            Node objectNode =
//                                    getOrCreateNodeWithUniqueFactory(s, db);
//                            individualNode.createRelationshipTo(objectNode,
//                                    DynamicRelationshipType.withName(reltype));
//                        }
//                    }
//                    for (OWLDataPropertyExpression dataProperty :
//                            ontology.getDataPropertiesInSignature()) {
//                        for (OWLLiteral object : reasoner.getDataPropertyValues(
//                                i, dataProperty.asOWLDataProperty())) {
//                            String reltype =
//                                    dataProperty.asOWLDataProperty().toString();
//                            reltype = reltype.substring(reltype.indexOf("#") + 1,
//                                    reltype.lastIndexOf(">"));
//                            String s = object.toString();
//                            individualNode.setProperty(reltype, s);
//                        }
//                    }
//                }
//            }
//            tx.success();
//        } finally {
////            tx.finish();
//            tx.close();
//        }
//    }
//
//    private static Node getOrCreateNodeWithUniqueFactory(String nodeName,
//                                                         GraphDatabaseService graphDb) {
//        UniqueFactory<Node> factory = new UniqueFactory.UniqueNodeFactory(
//                graphDb, "index") {
//            @Override
//            protected void initialize(Node created,
//                                      Map<String, Object> properties) {
//                for (String s : properties.keySet()) {
//                    created.setProperty(s, properties.get(s));
//                }
//            }
//        };
//
//        return factory.getOrCreate("n", nodeName);
//    }
}
//    START e=node:name(name="experiment123"), ag=node:name(name="Agent")
//    MATCH e-[r:hadActivity]->ac-->a-[:isA*]->ag
//    RETURN distinct e.name as experiment, type(r) as relationship, a.name as agent
//    ac.name as activity, ac.startedAtTime as starttime, ac.endedAtTime as endtime
//    ORDER BY starttime
