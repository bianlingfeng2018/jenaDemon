import edu.fudan.jenaDemon.NeoGraph;
import edu.fudan.jenaDemon.Utils;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.junit.Test;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import java.util.Iterator;

import static org.apache.jena.ontology.OntModelSpec.*;

public class JenaTest {
    @Test
    public void listRDFTypeTest() {
        // create the base model
        String SOURCE = "http://www.co-ode.org/ontologies/pizza/pizza.owl";
        String NS = SOURCE + "#";
        OntModel base = ModelFactory.createOntologyModel(OWL_MEM);
        base.read(Utils.getResourceAsStream("data/pizza.owl.rdf"), "RDF/XML");

        // create the reasoning model using the base
        OntModel inf = ModelFactory.createOntologyModel(OWL_MEM_MICRO_RULE_INF, base);

        // create a dummy paper for this example
        OntClass pizza = base.getOntClass(NS + "Pizza");
        Individual p1 = base.createIndividual(NS + "pizza_individual_1", pizza);

        // list the asserted types
        for (Iterator<Resource> i = p1.listRDFTypes(false); i.hasNext(); ) {
            System.out.println(p1.getURI() + " is asserted in class " + i.next());
        }

        // list the inferred types
        p1 = inf.getIndividual(NS + "pizza_individual_1");
        for (Iterator<Resource> i = p1.listRDFTypes(false); i.hasNext(); ) {
            System.out.println(p1.getURI() + " is inferred to be in class " + i.next());
        }
    }

    @Test
    public void printClassTreeTest() {
        OntModel base = ModelFactory.createOntologyModel(OWL_MEM);
        base.read(Utils.getResourceAsStream("data/pizza_inferred.owl.rdf"), "RDF/XML");
        OntClass ontClass = base.getOntClass("http://www.co-ode.org/ontologies/pizza/pizza.owl#VegetarianPizza");
        printOntClassNode(ontClass, 0);
    }
    //OntClass:VegetarianPizza Depth:0
    //  OntClass:VegetarianPizzaEquivalent2 Depth:1
    //    OntClass:Veneziana Depth:2
    //    OntClass:PrinceCarlo Depth:2
    //    OntClass:Caprina Depth:2
    //    OntClass:QuattroFormaggi Depth:2
    //    OntClass:Rosa Depth:2
    //    OntClass:Mushroom Depth:2
    //    OntClass:Soho Depth:2
    //    OntClass:Giardiniera Depth:2
    //    OntClass:Fiorentina Depth:2
    //    OntClass:Margherita Depth:2
    //  OntClass:VegetarianPizzaEquivalent1 Depth:1
    //    OntClass:PrinceCarlo Depth:2
    //    OntClass:QuattroFormaggi Depth:2
    //    OntClass:Margherita Depth:2
    //    OntClass:Mushroom Depth:2
    //    OntClass:Giardiniera Depth:2
    //    OntClass:Fiorentina Depth:2
    //    OntClass:Veneziana Depth:2
    //    OntClass:Rosa Depth:2
    //    OntClass:Caprina Depth:2
    //    OntClass:Soho Depth:2

    private static void printOntClassNode(OntClass oc, int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.printf("  ");
        }
        System.out.println("OntClass:" + oc.getLocalName() + " Depth:" + depth);
        //print all subclass
        if (oc.hasSubClass()) {
            for (Iterator<OntClass> it = oc.listSubClasses(true); it.hasNext(); ) {
                OntClass c = it.next();
                printOntClassNode(c, depth + 1);
            }
        }
        //print all Individual
        for (Iterator<Individual> i = (Iterator<Individual>) oc.listInstances(true); i.hasNext(); ) {
            Individual ind = i.next();
            int deeper = depth + 1;
            System.out.println("Individual:" + ind.getLocalName() + " Depth:" + deeper);
        }
        return;
    }

    @Test
    public void listPropertyTest() {
        // create the base model
        String SOURCE = "http://www.co-ode.org/ontologies/pizza/pizza.owl";
        String NS = SOURCE + "#";
        OntModel base = ModelFactory.createOntologyModel(OWL_MEM);
        base.read(Utils.getResourceAsStream("data/pizza.owl.rdf"), "RDF/XML");
        for (ExtendedIterator<ObjectProperty> i = base.listObjectProperties(); i.hasNext(); ) {
            System.out.println(i.next());
        }
    }

    @Test
    public void listIndividualsTest() {
        // create the base model
        String SOURCE = "http://www.co-ode.org/ontologies/pizza/pizza.owl";
        String NS = SOURCE + "#";
        OntModel base = ModelFactory.createOntologyModel(OWL_MEM);
        base.read(Utils.getResourceAsStream("data/pizza.owl.rdf"), "RDF/XML");
        for (ExtendedIterator<Individual> i = base.listIndividuals(); i.hasNext(); ) {
            System.out.println(i.next());
        }
    }

    @Test
    public void listClassesTest() {
        // create the base model
        String SOURCE = "http://www.co-ode.org/ontologies/pizza/pizza.owl";
        String NS = SOURCE + "#";
        OntModel base = ModelFactory.createOntologyModel(OWL_MEM);
        base.read(Utils.getResourceAsStream("data/pizza.owl.rdf"), "RDF/XML");
        for (ExtendedIterator<OntClass> i = base.listClasses(); i.hasNext(); ) {
            OntClass ontClass = i.next();
            System.out.println(ontClass.toString() + " isSubClassOf " +
                    (ontClass.getSuperClass() == null ? null : ontClass.getSuperClass().toString()));
        }
    }

    @Test
    public void listRestrictionTest() {
        // create the base model
        String SOURCE = "http://www.co-ode.org/ontologies/pizza/pizza.owl";
        String NS = SOURCE + "#";
        OntModel base = ModelFactory.createOntologyModel(OWL_MEM);
        base.read(Utils.getResourceAsStream("data/pizza.owl.rdf"), "RDF/XML");
        for (ExtendedIterator<Restriction> i = base.listRestrictions(); i.hasNext(); ) {
            Restriction restriction = i.next();
            System.out.println(restriction.getOnProperty() + " " + (restriction.isSomeValuesFromRestriction() ? restriction.asSomeValuesFromRestriction().getSomeValuesFrom() : "other restriction"));
        }
    }

    @Test
    public void listStatementsTest() {
        // create the base model
//        String SOURCE = "http://www.co-ode.org/ontologies/pizza/pizza.owl";
//        String SOURCE = "http://www.semanticweb.org/bianlingfeng/ontologies/2019/4/pizza_inf.owl.rdf";
//        String NS = SOURCE + "#";
        OntModel base = ModelFactory.createOntologyModel(OWL_MEM);
//        base.read(Utils.getResourceAsStream("data/pizza_inferred_full.owl.rdf"), "RDF/XML");
        base.read(Utils.getResourceAsStream("data/pizza.owl.rdf"), "RDF/XML");
        for (StmtIterator i = base.listStatements(); i.hasNext(); ) {
            Statement st = i.next();
            System.out.println(st);
        }
    }


    @Test
    public void listInfoClassByClassTest() {
        // create the base model
        String SOURCE = "http://www.co-ode.org/ontologies/pizza/pizza.owl";
        String NS = SOURCE + "#";
        OntModel base = ModelFactory.createOntologyModel(OWL_MEM);
        base.read(Utils.getResourceAsStream("data/pizza.owl.rdf"), "RDF/XML");
        for (ExtendedIterator<OntClass> i = base.listClasses(); i.hasNext(); ) {
            OntClass ontClass = i.next();
            String comment = ontClass.getComment("en");
            String label = ontClass.getLabel("pt");
            if (ontClass.isAnon()) {  // 输出类 Resource URI 或者 bNode
//                System.out.println();
//                System.out.println("Blank Node: " + ontClass.toString());
            } else {
                System.out.println();
                System.out.println(ontClass.toString());
            }
            System.out.println("label : " + label);  // 输出注释
            System.out.println("comment : " + comment);
            if (ontClass.getEquivalentClass() != null) {  // 输出等价类 URI
                System.out.println("equivalentClass : ");
                OntClass equivalentClass = ontClass.getEquivalentClass();
                if (equivalentClass.isIntersectionClass()) {  // 交集
                    System.out.println("000");
                } else if (equivalentClass.isUnionClass()) {  // 并集
                    System.out.println("111");
                } else if (equivalentClass.isComplementClass()) {  // 补集
                    System.out.println("222");
                } else if (equivalentClass.isEnumeratedClass()) {  // 枚举类型
                    System.out.println("333");
                } else {
                    System.out.println("other equivalentClass");
                }
            }
            for (ExtendedIterator<OntClass> k = ontClass.listDisjointWith(); k.hasNext(); ) {  // 输出互斥类 URI
                System.out.println("disJoinWith : " + k.next().toString());
            }
            for (ExtendedIterator<OntClass> l = ontClass.listSuperClasses(); l.hasNext(); ) {  // 输出 superClass
                OntClass superClass = l.next();
                System.out.println("superClass : " + superClass.toString());
                if (superClass.isRestriction()) {  // superClass 可能是无名字的 Restriction 或者有名字的 Class
                    System.out.println("Restriction : ");
                    Restriction restriction = superClass.asRestriction();
                    if (restriction.isSomeValuesFromRestriction()) {
                        System.out.println(restriction.getOnProperty() + " " + restriction.asSomeValuesFromRestriction().getSomeValuesFrom());
                    } else if (restriction.isAllValuesFromRestriction()) {
                        System.out.println(restriction.getOnProperty() + " " + restriction.asAllValuesFromRestriction().getAllValuesFrom());
                    } else if (restriction.isCardinalityRestriction()) {
                        System.out.println(restriction.getOnProperty() + " " + restriction.asCardinalityRestriction().getCardinality());
                    } else if (restriction.isMaxCardinalityRestriction()) {
                        System.out.println(restriction.getOnProperty() + " " + restriction.asMaxCardinalityRestriction().getMaxCardinality());
                    } else if (restriction.isMinCardinalityRestriction()) {
                        System.out.println(restriction.getOnProperty() + " " + restriction.asMinCardinalityRestriction().getMinCardinality());
                    } else if (restriction.isHasValueRestriction()) {
                        System.out.println(restriction.getOnProperty() + " " + restriction.asHasValueRestriction().getHasValue());
                    } else {
                        System.out.println("other restriction");
                    }
                }
                if (superClass.isIntersectionClass()) {  // 交集
                    System.out.println("444");
                } else if (superClass.isUnionClass()) {  // 并集
                    System.out.println("555");
                } else if (superClass.isComplementClass()) {  // 补集
                    System.out.println("666");
                } else if (superClass.isEnumeratedClass()) {  // 枚举类型
                    System.out.println("777");
                } else if (superClass.isAnon()) {
                    System.out.println("888");
                } else {
                    System.out.println("other superClass");
                }
            }
        }
    }

    @Test
    public void loadGraphModel() {
        // neo4j driver and session init
//        Driver driver = GraphDatabase.driver("bolt://192.168.0.147:7687");
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "123456"));
        Session session = driver.session();
        try {
            // create the base model
            OntModel base = ModelFactory.createOntologyModel(OWL_MEM);
//            base.read(Utils.getResourceAsStream("data/pizza_inferred_full.owl.rdf"), "RDF/XML");
            base.read(Utils.getResourceAsStream("data/accident.rdf"), "RDF/XML");
//        Graph graph = base.getGraph();
//        for (ExtendedIterator<Triple> i = graph.find();i.hasNext();){
//            System.out.println(i.next());
//        }
//        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
//        GraphDatabaseService db= dbFactory.newEmbeddedDatabase(new File("/Users/bianlingfeng/IdeaProjects/jenaDemon/Neo4jDataBase"));
            NeoGraph neoGraph = new NeoGraph(session, base);
            Model graph = ModelFactory.createModelForGraph(neoGraph);
            Model graphModel = graph.add(base);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
            driver.close();
        }
    }

    @Test
    public void restrictionClassExpressionTest() {
        OntModel m = ModelFactory.createOntologyModel(OWL_MEM);
        m.read(Utils.getResourceAsStream("data/accident.rdf"), "RDF/XML");
        OntClass ontClass = m.getOntClass("http://www.semanticweb.org/bianlingfeng/ontologies/2019/4/accident#事故");
//        Iterator<Restriction> i = m.listRestrictions();
//        while (i.hasNext()) {
//            Restriction r = i.next();
//            if (isTheOne(r)) {
//                // handle r
//            }
//        }
        for (Iterator<OntClass> i = ontClass.listSuperClasses(true); i.hasNext(); ) {
            OntClass c = i.next();

            if (c.isRestriction()) {
                Restriction r = c.asRestriction();

                if (r.isAllValuesFromRestriction()) {
                    AllValuesFromRestriction av = r.asAllValuesFromRestriction();
                    System.out.println("AllValuesFrom class " +
                            av.getAllValuesFrom().getURI() +
                            " on property " + av.getOnProperty().getURI());
                } else if (r.isSomeValuesFromRestriction()) {
                    SomeValuesFromRestriction sv = r.asSomeValuesFromRestriction();
                    System.out.println("SomeValuesFrom class " +
                            sv.getSomeValuesFrom().getURI() +
                            " on property " + sv.getOnProperty().getURI());
                } else {
                    System.out.println(r);
                }
            }
        }
    }

    private boolean isTheOne(Restriction r) {
        return "事故".equals(r.getSubClass().getLocalName());
    }

    @Test
    public void DeclaredPropertiesTest() {
        OntModel m = ModelFactory.createOntologyModel(OWL_MEM);
        m.read(Utils.getResourceAsStream("data/accident.rdf"), "RDF/XML");
        OntClass ontClass = m.getOntClass("http://www.semanticweb.org/bianlingfeng/ontologies/2019/4/accident#事故");
        ExtendedIterator<OntProperty> properties = ontClass.listDeclaredProperties();
        while (properties.hasNext()) {
            OntProperty p = properties.next();
            System.out.println(p.getDomain());
            System.out.println(p.getRange());
        }
    }
}
