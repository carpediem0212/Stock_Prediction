package kr.ac.jbnu.sql.stock.persistance;

import java.util.HashMap;

import kr.ac.jbnu.sql.stock.Constants;

public class PersistenceFactory
{
	private HashMap<String, AbstractPersistence> persistenceClasses = new HashMap<String, AbstractPersistence>();

	private static PersistenceFactory instance;

	private PersistenceFactory()
	{
	}

	public static PersistenceFactory getInstance()
	{
		if (instance == null)
		{
			instance = new PersistenceFactory();
		}
		return instance;
	}

	public AbstractPersistence getPersistence()
	{
		String packageName = PersistenceFactory.class.getPackage().toString();
		String persistenceClass = packageName + "." + Constants.persistence + "Persistence";

		AbstractPersistence ap = persistenceClasses.get(persistenceClass);
		if (ap == null)
		{
			try
			{
				Class clazz = Class.forName(persistenceClass);
				ap = (AbstractPersistence) clazz.newInstance();
				persistenceClasses.put(persistenceClass, ap);
				return ap;
			} catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			} catch (InstantiationException e)
			{
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

}
