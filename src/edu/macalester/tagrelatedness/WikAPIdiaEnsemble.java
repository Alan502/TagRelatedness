package edu.macalester.tagrelatedness;

import org.wikapidia.conf.ConfigurationException;
import org.wikapidia.conf.Configurator;
import org.wikapidia.core.cmd.Env;
import org.wikapidia.core.cmd.EnvBuilder;
import org.wikapidia.core.dao.DaoException;
import org.wikapidia.core.dao.LocalPageDao;
import org.wikapidia.core.lang.Language;
import org.wikapidia.sr.MonolingualSRMetric;
import org.wikapidia.sr.SRResult;

public class WikAPIdiaEnsemble implements TagSimilarityMeasure {
	
	MonolingualSRMetric sr = null;
	
	public WikAPIdiaEnsemble(String wikAPidiaInstallDir){
		try {
			Env env = new EnvBuilder().setBaseDir(wikAPidiaInstallDir).build();
			Configurator conf = env.getConfigurator();
			
			Language simple = Language.getByLangCode("simple");

			sr = conf.get(
			        MonolingualSRMetric.class, "ensemble",
			        "language", simple.getLangCode());
			
		} catch (ConfigurationException e) {
			System.out.println("Configuration Exception: "+e.getMessage());
		}
	}

	@Override
	public double calculateSimilarity(String tag1, String tag2) {
		SRResult s = null;
		try {
			s = sr.similarity(tag1,tag2,true);
		} catch (DaoException e) {
			System.out.println("Dao Exception: "+e.getMessage());
			e.printStackTrace();
		}
		return s.getScore();
	}

}
