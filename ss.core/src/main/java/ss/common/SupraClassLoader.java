package ss.common;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.lang.String;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import ss.global.SSLogger;


public class SupraClassLoader extends ClassLoader {
	private static final Logger logger = SSLogger.getLogger(SupraClassLoader.class);
    public SupraClassLoader(){
              super(SupraClassLoader.class.getClassLoader());
    }
    @SuppressWarnings("unchecked")
	public Class loadClass(String className) throws ClassNotFoundException {
              return findClass(className);
    }
    @SuppressWarnings("unchecked")
	public Class findClass(String className){
              byte classByte[];
              Class result=null;
              result = (Class)this.classes.get(className);
              if(result != null){
                        return result;
              }
              try{
                        return findSystemClass(className);
              }catch(Exception e){
              }
              try{
                        String classPath =
                        ((String)ClassLoader.getSystemResource(className.replace('.',File.separatorChar)+".class").getFile()).substring(1);
                        classByte = loadClassData(classPath);
                        result = defineClass(className,classByte,0,classByte.length,null);
                        this.classes.put(className,result);
                        return result;
              }catch(Exception e){
                        return null;
              }
    }
    private byte[] loadClassData(String className) throws IOException{
              File f ;
              f = new File(className);
              int size = (int)f.length();
              byte buff[] = new byte[size];
              FileInputStream fis = new FileInputStream(f);
              DataInputStream dis = new DataInputStream(fis);
              dis.readFully(buff);
              dis.close();
              return buff;
    }
    private Hashtable classes = new Hashtable();



@SuppressWarnings("unchecked")
public static void main(String[] args) {
	
                  SupraClassLoader test = new SupraClassLoader();
                  try {
					test.loadClass("ss.client.ui.WelcomeScreen");
					
					//WelcomeScreen welcome = test.findClass("ss.client.ui.WelcomeScreen");
					//Class.forName("WelcomeScreen");
					//StaticBlock sb = new StaticBlock();
					//sb.main();
					logger.info("classloaded!!");
					try {
						
					    
					      
					       
						(Class.forName("ss.client.ui.WelcomeScreen")).newInstance();
						
						Class cls = Class.forName("ss.client.ui.WelcomeScreen");
//						 types of the constructor arguments
						Class[] arguments = new Class[] {String.class};
						Constructor cnstr =null;
						try {
							cnstr = cls.getConstructor(arguments);
						} catch (SecurityException e) {
							logger.error(e.getMessage(), e);
						} catch (NoSuchMethodException e) {
							logger.error(e.getMessage(), e);
						}
//						 objects you want to pass to the constructor
						Object[] realArguments = new Object[] {"servant"};
						
						try {
							//@SuppressWarnings("unused")
							Object myObject = cnstr.newInstance(realArguments);
						} catch (IllegalArgumentException e) {
							logger.error(e.getMessage(), e);
						} catch (InvocationTargetException e) {
							logger.error(e.getMessage(), e);
						}
						
					} catch (InstantiationException e) {
						logger.error(e.getMessage(), e);
					} catch (IllegalAccessException e) {
						logger.error(e.getMessage(), e);
					}
					
					
					
				} catch (ClassNotFoundException e) {
					logger.error(e.getMessage(), e);
				}

}

}


