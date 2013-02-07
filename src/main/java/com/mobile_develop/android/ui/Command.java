package com.mobile_develop.android.ui;


public class Command implements Comparable<Command>{
	
	public static enum CommandStyle {
		Normal, 
		Menu,
		Home,
		Back,
        Forward,
		Search, 
		Help, 
		Quit,
        MoreOnly,
        Secret
		
	}
	
	public static class CommandType {
		private String name;
		private String declaringClass;
        private int id;
		
		public CommandType( int id, String name, String declaringClass ) {
            this.id = id;
			this.name = name;
			this.declaringClass = declaringClass;
		}
		
		public String toString() {
			return this.declaringClass+":"+this.name;
		}

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }
	}
	
	private CommandType type;
	private Action action;
	private CommandStyle style;
	private int priority;
	private boolean enabled;
	
	public Command( CommandType type, Action action, CommandStyle style, int priority, boolean enabled ) {
		this.type = type;
		this.action = action;
		this.style = style;
		this.priority = priority;
		this.enabled = enabled;
	}
	
	public CommandType getType() {
		return this.type;
	}
	
	public Action getAction() {
		return this.action;
	}
	
	public CommandStyle getStyle() {
		return this.style;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

	@Override
	public int compareTo(Command o) {
		return this.priority - o.priority;
	}

}
