package edu.macalester.tagrelatedness;

import org.wikibrain.conf.ConfigurationException;
import org.wikibrain.conf.Configurator;
import org.wikibrain.core.cmd.Env;
import org.wikibrain.core.cmd.EnvBuilder;
import org.wikibrain.core.dao.DaoException;
import org.wikibrain.core.lang.Language;
import org.wikibrain.sr.MonolingualSRMetric;
import org.wikibrain.sr.SRResult;
/**
 * The WikAPIdiaEnsemble similarity measure. This similarity measure uses the WikAPIdia library to calculate the similarity measure between two tags.
 * For more information visit: https://github.com/shilad/wikAPIdia
 * @author alan
 *
 */
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
	/**
	 * Calculates the similarity between two tags using WikAPIdia.
	 * @param tag1 the first tag
	 * @param tag2 the second tags
	 */
	
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
