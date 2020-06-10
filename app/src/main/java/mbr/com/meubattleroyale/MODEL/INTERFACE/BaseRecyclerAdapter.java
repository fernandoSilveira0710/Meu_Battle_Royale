package mbr.com.meubattleroyale.MODEL.INTERFACE;

import java.util.List;

public interface BaseRecyclerAdapter<T> {
    void clear(boolean notifyDataSetChanged);

    void add(T item);

    void addAll(List<T> items);

    boolean isEmpty();
}
