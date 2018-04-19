package net.hycrafthd.basiclavamusicplayer.event;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MusicEventSubscriber {
	
}