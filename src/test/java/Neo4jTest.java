import org.junit.Test;
import org.neo4j.driver.v1.*;
import java.util.Map;

import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jTest {
    @Test
    public void deleteAllTest(){
//        Driver driver = GraphDatabase.driver( "bolt://10.222.126.62:7687");
        Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "123456" ) );
        Session session = driver.session();
        session.run( "MATCH (n) DETACH DELETE n",
                parameters() );

        StatementResult result = session.run( "MATCH (n)" +
                        "RETURN n",
                parameters() );
        while ( result.hasNext() )
        {
            Record record = result.next();
            Map<String, Object> map = record.asMap();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }
        session.close();
        driver.close();
    }

    @Test
    public void addNodeTest(){
        Driver driver = GraphDatabase.driver( "bolt://192.168.0.147:7687");
        Session session = driver.session();
        session.run( "CREATE (a:Person {name: {name}, title: {title}})",
                parameters( "name", "Arthur001", "title", "King001" ) );

        StatementResult result = session.run( "MATCH (a:Person) WHERE a.name = {name} " +
                        "RETURN a.name AS name, a.title AS title",
                parameters( "name", "Arthur001" ) );
        while ( result.hasNext() )
        {
            Record record = result.next();
            System.out.println( record.get( "title" ).asString() + " " + record.get( "name" ).asString() );
        }
        session.close();
        driver.close();
    }

    @Test
    public void addTripleTest(){
        Driver driver = GraphDatabase.driver( "bolt://192.168.0.147:7687");
        Session session = driver.session();
        session.run("CREATE (p1:Profile1{name:'name1'})-[r1:LIKES {name:'name2'}]->(p2:Profile2{name:'name3'})",
                parameters());

        session.close();
        driver.close();
    }

    @Test
    public void selectTest(){
        Driver driver = GraphDatabase.driver( "bolt://192.168.0.147:7687");
        Session session = driver.session();
        StatementResult result = session.run("MATCH (a:Person) " +
                        "WHERE a.name = 'Arthur001' " +
                        "RETURN count(1)",
                parameters());
        while ( result.hasNext() )
        {
            Record record = result.next();
            Map<String, Object> map = record.asMap();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }
        session.close();
        driver.close();
    }

    @Test
    public void mergeRelationShipAndNodeTest(){
        Driver driver = GraphDatabase.driver( "bolt://192.168.0.147:7687");
        Session session = driver.session();
        session.run("MATCH (n:`Profile1` {id:'name5'}) SET n += {age:12}",  // 添加属性
                parameters());
//        session.run("MERGE (n:`Profile1` {id:'name1'});",  // 添加点（不可重复）
//                parameters());
//        session.run("MATCH (src:`Profile1` {id:'name1'}), (dest:`Profile2`{id:'name4'}) MERGE (src)-[:`DISLIKE`]->(dest);",  // 添加边（不可重复）
//                parameters());
        session.close();
        driver.close();
    }
}
