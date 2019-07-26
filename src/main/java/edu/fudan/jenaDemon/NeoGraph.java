package edu.fudan.jenaDemon;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.GraphBase;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.graph.GraphSPARQLService;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.neo4j.driver.v1.Values.parameters;

public class NeoGraph extends GraphBase {
    private static Logger log = LoggerFactory.getLogger(GraphSPARQLService.class);
    Session session;

    public NeoGraph(Session session) {
        this.session = session;
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
        if(o.isBlank() || s.isBlank()){
            return;
        }
        if ("type".equals(p.getLocalName()) && "Class".equals(o.toString().substring(o.toString().indexOf("#") + 1))) {
            session.run("MERGE (n:`AccidentNode` {id:{id},name:{name}});",  // 添加起点（不可重复）
                    parameters("id", s.toString(),
                            "name", s.isBlank() ? "Blank Node" : (s.isLiteral() ? s.toString() : s.getLocalName())));
            session.run("MATCH (n:`AccidentNode` {id:{id},name:{name}}) SET n += {type:'Class'};",  // 添加属性 Type:Class
                    parameters("id", s.toString(),
                            "name", s.isBlank() ? "Blank Node" : (s.isLiteral() ? s.toString() : s.getLocalName())));
        } else if ("type".equals(p.getLocalName()) && "NamedIndividual".equals(o.toString().substring(o.toString().indexOf("#") + 1))) {
            session.run("MERGE (n:`AccidentNode` {id:{id},name:{name}});",  // 添加起点（不可重复）
                    parameters("id", s.toString(),
                            "name", s.isBlank() ? "Blank Node" : (s.isLiteral() ? s.toString() : s.getLocalName())));
            session.run("MATCH (n:`AccidentNode` {id:{id},name:{name}}) SET n += {type:'NamedIndividual'};",  // 添加属性 Type:NamedIndividual
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

    @Override
    public void performDelete(Triple t) {
        super.performDelete(t);
    }
}
