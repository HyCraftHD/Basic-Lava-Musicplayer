package net.hycrafthd.basiclavamusicplayer.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class MusicPlayerEventBus {
	
	private static ConcurrentHashMap<Object, ArrayList<Method>> listenerlist = new ConcurrentHashMap<Object, ArrayList<Method>>();
	
	public static void register(Object obj) {
		if (listenerlist.containsKey(obj)) {
			return;
		}
		ArrayList<Method> methods = new ArrayList<Method>();
		for (Method method : obj.getClass().getDeclaredMethods()) {
			method.setAccessible(true);
			if (method.isAnnotationPresent(MusicEventSubscriber.class)) {
				if (method.getParameterCount() == 1) {
					if (MusicPlayerEvent.class.isAssignableFrom(method.getParameters()[0].getType())) {
						methods.add(method);
					}
				}
			}
		}
		listenerlist.put(obj, methods);
	}
	
	public static void unregister(Object obj) {
		if (listenerlist.containsKey(obj)) {
			listenerlist.remove(obj);
		}
	}
	
	public static void post(MusicPlayerEvent event) {
		Enumeration<Object> enumeration = listenerlist.keys();
		while (enumeration.hasMoreElements()) {
			Object obj = enumeration.nextElement();
			for (Method method : listenerlist.get(obj)) {
				method.setAccessible(true);
				if (method.getParameters()[0].getType().equals(event.getClass())) {
					try {
						method.invoke(obj, event);
					} catch (Exception ex) {
						System.out.println("Failed posting event " + event + " to method " + method);
						ex.printStackTrace();
					}
				}
			}
		}
	}
	
}
