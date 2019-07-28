package edu.fudan.jenaDemon;

import org.apache.jena.graph.BlankNodeId;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.GraphBase;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.graph.GraphSPARQLService;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

import static org.neo4j.driver.v1.Values.parameters;

public class NeoGraph extends GraphBase {
    private static Logger log = LoggerFactory.getLogger(GraphSPARQLService.class);
    Session session;
    OntModel base;

    public NeoGraph(Session session, OntModel base) {
        this.session = session;
        this.base = base;
    }

    @Override
    protected ExtendedIterator<Triple> graphBaseFind(Triple m) {
        Node s = m.getMatchSubject();
        Var sVar = null;
        if (s == null) {
            sVar = Var.alloc("s");
            s = sVar;
        }

        Node p = m.getMatchPredicate();
        Var pVar = null;
        if (p == null) {
            pVar = Var.alloc("p");
            p = pVar;
        }

        Node o = m.getMatchObject();
        Var oVar = null;
        if (o == null) {
            oVar = Var.alloc("o");
            o = oVar;
        }

        Triple triple = new Triple(s, p, o);

        // Evaluate as an algebra expression
        BasicPattern pattern = new BasicPattern();
        pattern.add(triple);
        Op op = new OpBGP(pattern);

//        // Make remote execution object.
//        System.err.println("GraphSPARQLService.graphBaseFind: Unimplemented : remote service execution") ;
//        //Plan plan = factory.create(op, getDataset(), BindingRoot.create(), null) ;
//
//        QueryIterator qIter = plan.iterator() ;
//        List<Triple> triples = new ArrayList<Triple>() ;
//
//
//        for (; qIter.hasNext() ; )
//        {
//            Binding b = qIter.nextBinding() ;
//            Node sResult = s ;
//            Node pResult = p ;
//            Node oResult = o ;
//            if ( sVar != null )
//                sResult = b.get(sVar) ;
//            if ( pVar != null )
//                pResult = b.get(pVar) ;
//            if ( oVar != null )
//                oResult = b.get(oVar) ;
//            Triple resultTriple = new Triple(sResult, pResult, oResult) ;
//            if ( log.isDebugEnabled() )
//                log.debug("  "+resultTriple) ;
//            triples.add(resultTriple) ;
//        }
//        qIter.close() ;
//        return WrappedIterator.createNoRemove(triples.iterator()) ;
        return null;
    }

    @Override
    protected int graphBaseSize() {
        return super.graphBaseSize();
    }

    @Override
    public void performAdd(Triple t) {
//        super.performAdd(t);
        Node s = t.getSubject();
        Node p = t.getPredicate();
        Node o = t.getObject();
//        System.out.println(t.toString());
        if (o.isBlank()) {  // 舍去 blankNode
            return;
        }
        if (s.isBlank()) {  // 舍去 blankNode
            return;
        }
        // 对于 xxx type Class 三元组，Class 仅仅作为属性，而非节点
        if ("type".equals(p.getLocalName()) && "Class".equals(o.toString().substring(o.toString().indexOf("#") + 1))) {
            session.run("MERGE (n:`AccidentNode` {id:{id},name:{name}});",  // 添加起点（不可重复）
                    parameters("id", s.toString(),
                            "name", s.isBlank() ? "Blank Node" : (s.isLiteral() ? s.toString() : s.getLocalName())));
            session.run("MATCH (n:`AccidentNode` {id:{id},name:{name}}) SET n += {type:'Class'};",  // 添加属性 Type:Class
                    parameters("id", s.toString(),
                            "name", s.isBlank() ? "Blank Node" : (s.isLiteral() ? s.toString() : s.getLocalName())));

            handleClassRestriction(s, p, o);
        } else if ("type".equals(p.getLocalName()) && "NamedIndividual".equals(o.toString().substring(o.toString().indexOf("#") + 1))) {
            session.run("MERGE (n:`AccidentNode` {id:{id},name:{name}});",  // 添加起点（不可重复）
                    parameters("id", s.toString(),
                            "name", s.isBlank() ? "Blank Node" : (s.isLiteral() ? s.toString() : s.getLocalName())));
            session.run("MATCH (n:`AccidentNode` {id:{id},name:{name}}) SET n += {type:'NamedIndividual'};",  // 添加属性 Type:NamedIndividual
                    parameters("id", s.toString(),
                            "name", s.isBlank() ? "Blank Node" : (s.isLiteral() ? s.toString() : s.getLocalName())));
        } else if ("type".equals(p.getLocalName()) && "DatatypeProperty".equals(o.toString().substring(o.toString().indexOf("#") + 1))) {
            session.run("MERGE (n:`AccidentNode` {id:{id},name:{name}});",  // 添加起点（不可重复）
                    parameters("id", s.toString(),
                            "name", s.isBlank() ? "Blank Node" : (s.isLiteral() ? s.toString() : s.getLocalName())));
            session.run("MATCH (n:`AccidentNode` {id:{id},name:{name}}) SET n += {type:'DatatypeProperty'};",  // 添加属性 Type:DatatypeProperty
                    parameters("id", s.toString(),
                            "name", s.isBlank() ? "Blank Node" : (s.isLiteral() ? s.toString() : s.getLocalName())));
        } else if ("type".equals(p.getLocalName()) && "Ontology".equals(o.toString().substring(o.toString().indexOf("#") + 1))) {
            session.run("MERGE (n:`AccidentNode` {id:{id},name:{name}});",  // 添加起点（不可重复）
                    parameters("id", s.toString(),
                            "name", s.isBlank() ? "Blank Node" : (s.isLiteral() ? s.toString() : s.getLocalName())));
            session.run("MATCH (n:`AccidentNode` {id:{id},name:{name}}) SET n += {type:'Ontology'};",  // 添加属性 Type:Ontology
                    parameters("id", s.toString(),
                            "name", s.isBlank() ? "Blank Node" : (s.isLiteral() ? s.toString() : s.getLocalName())));
        } else if ("type".equals(p.getLocalName()) && "ObjectProperty".equals(o.toString().substring(o.toString().indexOf("#") + 1))) {
            session.run("MERGE (n:`AccidentNode` {id:{id},name:{name}});",  // 添加起点（不可重复）
                    parameters("id", s.toString(),
                            "name", s.isBlank() ? "Blank Node" : (s.isLiteral() ? s.toString() : s.getLocalName())));
            session.run("MATCH (n:`AccidentNode` {id:{id},name:{name}}) SET n += {type:'ObjectProperty'};",  // 添加属性 Type:ObjectProperty
                    parameters("id", s.toString(),
                            "name", s.isBlank() ? "Blank Node" : (s.isLiteral() ? s.toString() : s.getLocalName())));
        } else {
            session.run("MERGE (n:`AccidentNode` {id:{id},name:{name}});",  // 添加起点（不可重复）
                    parameters("id", s.toString(),
                            "name", (s.isBlank() ? "Blank Node" : (s.isLiteral() ? s.toString() : s.getLocalName()))));
            session.run("MERGE (n:`AccidentNode` {id:{id},name:{name}});",  // 添加终点（不可重复）
                    parameters("id", o.toString(),
                            "name", (o.isBlank() ? "Blank Node" : (o.isLiteral() ? o.toString() : o.getLocalName()))));
            session.run("MATCH (src:`AccidentNode` {id:{p1}}), (dest:`AccidentNode` {id:{p2}}) MERGE (src)-[e:`" + p.getLocalName() + "` {id:{p3}}]->(dest);",  // 添加边（不可重复）
                    parameters(
                            "p1", s.toString(),
                            "p2", o.toString(),
                            "p3", p.toString()));
        }
    }

    private void handleClassRestriction(Node s, Node p, Node o) {
        String uri = s.getURI();  // 筛选出有 restriction 的类
        OntClass ontClass = base.getOntClass(uri);
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
                    String blankNodeId = c.getId().getBlankNodeId().toString();
                    SomeValuesFromRestriction sv = r.asSomeValuesFromRestriction();
//                    System.out.println("SomeValuesFrom class " +
//                            sv.getSomeValuesFrom().getURI() +
//                            " on property " + sv.getOnProperty().getURI());
                    session.run("MERGE (n:`AccidentNode` {id:{id},name:{name}});",  // 添加起点（不可重复）
                            parameters("id", s.toString(),
                                    "name", (s.isBlank() ? "Blank Node" : (s.isLiteral() ? s.toString() : s.getLocalName()))));
                    //
                    String restrictionsId = s.getURI() + "_Restrictions";
                    session.run("MERGE (n:`AccidentNode` {id:{id},name:{name}});",  // 添加终点（不可重复）
                            parameters("id", restrictionsId, "name", "Restrictions" + Integer.toHexString(restrictionsId.hashCode())));

                    String restrictionId = s.getURI() + "_Restriction_" + sv.getSomeValuesFrom().getURI() + "_" + sv.getOnProperty().getURI();
                    session.run("MERGE (n:`AccidentNode` {id:{id},name:{name}});",  // 添加终点（不可重复）
                            parameters("id", restrictionId, "name", "Restriction" + Integer.toHexString(restrictionId.hashCode())));
                    session.run("MATCH (src:`AccidentNode` {id:{p1}}), (dest:`AccidentNode` {id:{p2}}) MERGE (src)-[e:`" + "hasRestrictions" + "` ]->(dest);",  // 添加边（不可重复）
                            parameters(
                                    "p1", s.toString(),
                                    "p2", restrictionsId));
                    session.run("MATCH (src:`AccidentNode` {id:{p1}}), (dest:`AccidentNode` {id:{p2}}) MERGE (src)-[e:`" + "hasRestriction" + "` ]->(dest);",  // 添加边（不可重复）
                            parameters(
                                    "p1", restrictionsId,
                                    "p2", restrictionId));
                    //
                    session.run("MERGE (n:`AccidentNode` {id:{id},name:{name}});",  // 添加终点（不可重复）
                            parameters("id", sv.getOnProperty().getURI(),
                                    "name", sv.getOnProperty().getLocalName()));
                    session.run("MERGE (n:`AccidentNode` {id:{id},name:{name}});",  // 添加终点（不可重复）
                            parameters("id", sv.getSomeValuesFrom().getURI(),
                                    "name", sv.getSomeValuesFrom().getLocalName()));
                    session.run("MATCH (src:`AccidentNode` {id:{p1}}), (dest:`AccidentNode` {id:{p2}}) MERGE (src)-[e:`" + "onProperty" + "` ]->(dest);",  // 添加边（不可重复）
                            parameters(
                                    "p1", restrictionId,
                                    "p2", sv.getOnProperty().getURI()));
                    session.run("MATCH (src:`AccidentNode` {id:{p1}}), (dest:`AccidentNode` {id:{p2}}) MERGE (src)-[e:`" + "someValuesFrom" + "` ]->(dest);",  // 添加边（不可重复）
                            parameters(
                                    "p1", restrictionId,
                                    "p2", sv.getSomeValuesFrom().getURI()));
                } else {
                    System.out.println(r);
                }
            }
        }
    }

    @Override
    public void performDelete(Triple t) {
        super.performDelete(t);
    }
}
