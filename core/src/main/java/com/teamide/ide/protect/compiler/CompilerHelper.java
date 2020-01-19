package com.teamide.ide.protect.compiler;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.teamide.util.FileUtil;
import com.teamide.util.IOUtil;

public class CompilerHelper {

	// java源文件存放目录
	private final File sourceDir;
	// 编译后class类文件存放目录
	private final File targetDir;
	// 需要加载的jar
	private final File jarDir;

	private String jars = "";

	private String encoding = "UTF-8";

	private final DiagnosticCollector<JavaFileObject> diagnostic = new DiagnosticCollector<JavaFileObject>();

	/**
	 * 
	 * @param sourceDir
	 *            java源文件存放目录
	 * @param targetDir
	 *            编译后class类文件存放目录
	 * @param jarDir
	 *            需要加载的jar
	 */
	public CompilerHelper(File sourceDir, File targetDir, File jarDir) {

		this.sourceDir = sourceDir;
		this.targetDir = targetDir;
		this.jarDir = jarDir;
	}

	public boolean compile(String path) {

		// 获取编译器实例
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		// 获取标准文件管理器实例
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		try {
			// 得到filePath目录下的所有java源文件
			List<File> files = FileUtil.loadAllFiles(path);
			List<File> sourceFileList = new ArrayList<File>();
			for (File file : files) {
				if (file.isFile() && file.getName().endsWith(".java")) {
					sourceFileList.add(file);
				}
			}
			// 没有java文件，直接返回
			if (sourceFileList.size() == 0) {
				return false;
			}
			// 获取要编译的编译单元
			Iterable<? extends JavaFileObject> compilationUnits = fileManager
					.getJavaFileObjectsFromFiles(sourceFileList);

			CompilationTask compilationTask = compiler.getTask(null, fileManager, getDiagnostic(), getOptions(), null,
					compilationUnits);
			// 运行编译任务
			return compilationTask.call();
		} finally {
			IOUtil.close(fileManager);
		}
	}

	public DiagnosticCollector<JavaFileObject> getDiagnostic() {

		return diagnostic;
	}

	/**
	 * 编译选项，在编译java文件时，编译程序会自动的去寻找java文件引用的其他的java源文件或者class。
	 * -sourcepath选项就是定义java源文件的查找目录， -classpath选项就是定义class文件的查找目录。
	 */
	public Iterable<String> getOptions() {

		Iterable<String> options = Arrays.asList("-encoding", encoding, "-classpath", getJarFiles(jarDir), "-d",
				targetDir.getAbsolutePath(), "-sourcepath", sourceDir.getAbsolutePath());
		return options;
	}

	/**
	 * 查找该目录下的所有的jar文件
	 *
	 * @param jarDir
	 * @throws Exception
	 */
	private String getJarFiles(File jarDir) {

		jars = "";
		File sourceFile = jarDir;
		// String jars="";
		if (sourceFile.exists()) {// 文件或者目录必须存在
			if (sourceFile.isDirectory()) {// 若file对象为目录
				// 得到该目录下以.java结尾的文件或者目录
				sourceFile.listFiles(new FileFilter() {

					public boolean accept(File pathname) {

						if (pathname.isDirectory()) {
							return true;
						} else {
							String name = pathname.getName();
							if (name.endsWith(".jar") ? true : false) {
								jars = jars + pathname.getPath() + ";";
								return true;
							}
							return false;
						}
					}
				});
			}
		}
		return jars;
	}

}
