import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class TestWikiWordTokenizer extends TestCase {
    public void testHasMoreElements() {
        WikiWordTokenizer t1 = new WikiWordTokenizer("qweqw qweqwe");
        assertEquals(t1.hasMoreElements(), true);
    }

    public void testNextElement() {
        WikiWordTokenizer t1 = new WikiWordTokenizer("qweqw qweqwe");
        assertEquals(t1.nextElement(), "qweqw");
    }

    public void testWhileLoop() {
        WikiWordTokenizer t1 = new WikiWordTokenizer("no, that is not what I need at all. as I said ");
        List<String> lst = new ArrayList<String>(10);
        while (t1.hasMoreElements()) {
            String s = t1.nextElement();
            lst.add(s);
        }
        assertEquals(lst.toString(), "[what, need, said]");
    }
}
