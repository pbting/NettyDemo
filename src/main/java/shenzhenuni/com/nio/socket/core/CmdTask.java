/**
 * All rights reserved. This material is confidential and proprietary to 7ROAD SQ team.
 */
package shenzhenuni.com.nio.socket.core;

/**
 * <pre>
 * 执行cmd
 * </pre>
 */
public class CmdTask<V,M> implements Runnable {

	private CmdTaskQueue<V,M> queue;
	private V v;
	private Command<V,M> cmd;
	protected Long createTime;
	protected M m ;
	public CmdTask(Command<V,M> cmd,V v,M m,CmdTaskQueue<V,M> queue) {
		this.cmd = cmd;
		this.queue = queue;
		this.v = v;
		this.m = m;
		createTime = System.currentTimeMillis();
	}

	public CmdTask(Command<V,M> cmd,V v,M m) {
		this.cmd = cmd;
		this.v = v;
		this.m = m;
		createTime = System.currentTimeMillis();
	}
	
	public void run() {
		if (queue != null) {
			long start = System.currentTimeMillis();
			try {
				cmd.execute(this.v,this.m);
				long end = System.currentTimeMillis();
				long interval = end - start;
				long leftTime = end - createTime;
				if (interval >= 1000) {
					System.err.println("execute action : " + this.toString() + ", interval : " + interval + ", leftTime : " + leftTime + ", size : " + queue.getQueue().size());
				}
			} catch (Exception e) {
				System.err.println("执行 command 异常, command : " + cmd.toString() + ", packet : " + this.m.toString()+e.getMessage());
			} finally {
				queue.dequeue(this);
			}
		}
	}
	public CmdTaskQueue<V,M> getCmdTaskQuence(){
		return this.queue;
	}
	
	public void setCmdTaskQueue(CmdTaskQueue<V, M> queue) {
		this.queue = queue;
	}

	@Override
	public String toString() {
		return cmd.toString() + ", packet : " + this.m.toString();
	}
}
