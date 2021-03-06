import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

public class WikiWordInfo implements WritableComparable<WikiWordInfo> {
    private int NUMBER_OF_DOCS;
    private int MAX_NUMBER_OF_RESULTS;
    private Float _idf;
    private Integer _totalFreq;
    private WikiWordInfoEntry[] entries;

    public WikiWordInfo(int NUMBER_OF_DOCS, int MAX_NUMBER_OF_RESULTS) throws NumberFormatException {
        if (NUMBER_OF_DOCS < 1) {
            throw new NumberFormatException("Negative or 0 number of documents");
        }
        if (MAX_NUMBER_OF_RESULTS < 0) {
            throw new ArithmeticException("Negative number of results");
        }
        this.NUMBER_OF_DOCS = NUMBER_OF_DOCS;
        this.MAX_NUMBER_OF_RESULTS = MAX_NUMBER_OF_RESULTS;
    }

    public WikiWordInfo() throws NumberFormatException {
        this(1, 0);
    }

    public Integer getTotalFreq() {
        return _totalFreq;
    }

    public Float getIdf() {
        return _idf;
    }

    public WikiWordInfoEntry[] getEntries() {
        return entries;
    }

    public void set(String id, Float tf, Integer freq) {
        set(new WikiWordInfoEntry(id, tf, freq));
    }

    public void set(WikiWordInfoEntry entry) {
        set(new WikiWordInfoEntry[]{entry});
    }

    public void set(WikiWordInfoEntry[] entries) {
        this.entries = entries;
        this._totalFreq = 0;
        for (WikiWordInfoEntry entry : entries) {
            this._totalFreq += entry.freq;
        }

        this._idf = (float) Math.log(NUMBER_OF_DOCS / entries.length);  /* ! IDF */
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(_totalFreq);
        out.writeInt(entries.length);
        for (WikiWordInfoEntry entry : entries) {
            out.writeUTF(entry.id);
            out.writeFloat(entry.tf);
            out.writeInt(entry.freq);
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        _totalFreq = in.readInt();
        int length = in.readInt();
        entries = new WikiWordInfoEntry[length];
        for (int i = 0; i < length; i++) {
            entries[i] = new WikiWordInfoEntry(in.readUTF(), in.readFloat(), in.readInt());
        }
    }

    @Override
    public int compareTo(WikiWordInfo o) {
        Integer l1 = entries.length;
        Integer l2 = o.entries.length;
        return l1.compareTo(l2);
    }

    @Override
    public String toString() {
        // docid1:tfidf1 \t docid2:tfidf2
        StringBuilder sb = new StringBuilder();

        TreeSet<WikiWordInfoEntry> sortedEntries = new TreeSet<WikiWordInfoEntry>(new Comparator<WikiWordInfoEntry>() {
            @Override
            public int compare(WikiWordInfoEntry o1, WikiWordInfoEntry o2) {
                float x = o1.tf * _idf;
                float y = o2.tf * _idf;
                return -1 * ((x < y) ? -1 : 1);  // without 0 - all elements are different!
            }
        });

        System.out.println(entries.length);
        Collections.addAll(sortedEntries, entries);

        int i = 0;
        int max = sortedEntries.size();
        System.out.println(max);
        if (MAX_NUMBER_OF_RESULTS != 0 && max > MAX_NUMBER_OF_RESULTS) {
            max = MAX_NUMBER_OF_RESULTS;
        }
        System.out.println(max);
        for (WikiWordInfoEntry entry : sortedEntries) {
            sb.append(entry.id);
            sb.append(':');
            sb.append(entry.tf);

            if (++i < max) {
                sb.append('\t');
            } else {
                break;
            }
        }

        return sb.toString();
    }

}
