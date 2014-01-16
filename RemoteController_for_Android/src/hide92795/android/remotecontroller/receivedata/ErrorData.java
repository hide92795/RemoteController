package hide92795.android.remotecontroller.receivedata;

import hide92795.android.remotecontroller.util.Message;

public class ErrorData extends ReceiveData {
	private static final long serialVersionUID = -3431516189243849380L;
	private final int message_id;
	private final String addtional_info;

	public ErrorData(String message_id) {
		this(message_id, "");
	}

	public ErrorData(String message_id, String addtional_info) {
		setSuccess(false);
		this.message_id = Message.getMessageID(message_id);
		this.addtional_info = addtional_info;
	}

	public int getMessageId() {
		return message_id;
	}

	public String getAddtionalInfo() {
		return addtional_info;
	}
}
