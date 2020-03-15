package com.teamide.ide.processor.repository;

import java.io.File;

import javax.tools.Diagnostic;

import com.teamide.ide.compiler.CompilerHelper;
import com.teamide.ide.processor.param.RepositoryProcessorParam;

public class RepositoryBuild extends RepositoryBase {

	public RepositoryBuild(RepositoryProcessorParam param) {

		super(param);
	}

	public void compiler(String projectPath, String path) {

		try {
			File sourceDir = this.param.getJavaFolder(projectPath);

			File targetDir = new File(this.param.getTargetFolder(projectPath), "classes");
			if (!targetDir.exists()) {
				// build();
			}

			File jarDir = new File(this.param.getTargetFolder(projectPath), "lib");

			this.param.getLog().info("compiler path " + path);
			this.param.getLog().info("compiler sourceDir " + sourceDir);
			this.param.getLog().info("compiler targetDir " + targetDir);
			this.param.getLog().info("compiler jarDir " + jarDir);
			CompilerHelper compilerHelper = new CompilerHelper(sourceDir, targetDir, jarDir);
			if (!compilerHelper.compile(path)) {
				this.param.getLog().error(path + "编译失败");
				for (Diagnostic<?> diagnostic : compilerHelper.getDiagnostic().getDiagnostics()) {
					this.param.getLog().error(diagnostic.getMessage(null));
				}
			} else {
				this.param.getLog().info(path + "编译成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
