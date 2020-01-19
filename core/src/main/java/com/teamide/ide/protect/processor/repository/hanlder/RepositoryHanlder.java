package com.teamide.ide.protect.processor.repository.hanlder;

import java.io.File;
import java.io.FileInputStream;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

public class RepositoryHanlder {

	static MavenXpp3Reader MAVEN_READER = new MavenXpp3Reader();

	public static Model getPomModel(File pomFile) {
		if (pomFile == null || !pomFile.exists()) {
			return null;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(pomFile);
			Model model = MAVEN_READER.read(fis);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
