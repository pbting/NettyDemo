package shenzhenuni.com.nio.socket.core.quence;

public abstract class Action implements Runnable {

	protected ActionQueue queue;
	protected Long createTime;
	
	public Action() {
		createTime = System.currentTimeMillis();
	}
	
	public Action(ActionQueue queue) {
		this.queue = queue;
		createTime = System.currentTimeMillis();
	}
	
	public void setActionQueue(ActionQueue queue){
		this.queue = queue;
	}
	
	public ActionQueue getActionQueue() {
		return queue;
	}

	public void run() {
		if (queue != null) {
			long start = System.currentTimeMillis();
			try {
				execute();
				long end = System.currentTimeMillis();
				long interval = end - start;
				long leftTime = end - createTime;
				if (interval >= 1000) {
					System.err.println("execute action : " + this.toString() + ", interval : " + interval + ", leftTime : " + leftTime + ", size : " + queue.getQueue().size());
				}
			} catch (Exception e) {
				System.err.println("run action execute exception. action : " + this.toString()+e.getMessage());
			} finally {
				queue.dequeue(this);
			}
		}
	}

	public abstract long execute() throws TaskExecuteException;
}
