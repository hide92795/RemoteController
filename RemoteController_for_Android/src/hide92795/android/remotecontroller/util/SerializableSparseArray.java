package hide92795.android.remotecontroller.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import android.util.SparseArray;

public class SerializableSparseArray<E extends Serializable> extends SparseArray<E> implements Externalizable, Cloneable {
	private static final long serialVersionUID = -8790029822712710098L;

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int num = in.readInt();
		for (int i = 0; i < num; i++) {
			int key = in.readInt();
			E data = (E) in.readObject();
			put(key, data);
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		int num = size();
		out.write(num);
		for (int i = 0; i < num; i++) {
			int key = keyAt(i);
			E data = valueAt(i);
			out.write(key);
			out.writeObject(data);
		}
	}

	@Override
	public SerializableSparseArray<E> clone() {
		return (SerializableSparseArray<E>) super.clone();
	}
}
