package com.teamide.protect.ide.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.teamide.util.StringUtil;
import com.teamide.ide.constant.IDEConstant;

public class GitProcessor {

	private final File root;

	private final String git_home;

	public static void main(String[] args) throws Exception {
		File root = new File("C:/Server/temp/git");
		GitProcessor processor = new GitProcessor(root);
		processor.init();
		processor.remoteRemove("test11");
		processor.remoteAdd("test11", "https://gitee.com/coospro/1111.git");
		processor.remote();
		// processor.pull("test11");
		// processor.remoteShow("test11");
		// processor.branchAdd("111");
		// processor.checkout("111", "test11/2.0");
		// processor.checkout("local/2.0", "test11/2.0");
		// processor.checkout("local/2.0", null);
		File file = new File(root, "" + System.currentTimeMillis());
		file.createNewFile();
		processor.add(file.getName());
		processor.commit("asda");
		processor.push("test11", "local/2.0", "2.0", "coospro", "ZLgit19910112");
		processor.branch();

	}

	public GitProcessor(File root) {
		this(null, root);
	}

	public GitProcessor(String git_home, File root) {
		if (!StringUtil.isEmpty(git_home)) {
			git_home = git_home.replaceAll("\\\\", "/");
			if (git_home.endsWith("/bin") || git_home.endsWith("/bin/")) {
				git_home = git_home.substring(0, git_home.lastIndexOf("/bin"));
			}
		}
		this.git_home = git_home;
		this.root = root;
	}

	public StringBuffer checkout(String branch, String remoteBranch) {
		List<String> commands = new ArrayList<String>();
		commands.add("checkout");
		if (!StringUtil.isEmpty(remoteBranch)) {
			commands.add("-b");
		}
		commands.add(branch);
		if (!StringUtil.isEmpty(remoteBranch)) {
			commands.add(remoteBranch);
		}
		StringBuffer result = process(commands);
		return result;
	}

	public StringBuffer reset() {
		List<String> commands = new ArrayList<String>();
		commands.add("reset");
		commands.add("HEAD^");
		StringBuffer result = process(commands);
		return result;
	}

	public StringBuffer add(String... paths) {
		List<String> commands = new ArrayList<String>();
		commands.add("add");
		if (paths != null) {
			for (String path : paths) {
				if (path != null) {
					commands.add(path);
				}
			}
		}
		StringBuffer result = process(commands);
		return result;
	}

	public StringBuffer commit(String message) {
		List<String> commands = new ArrayList<String>();
		commands.add("commit");
		commands.add("-m");
		commands.add(message);
		StringBuffer result = process(commands);
		return result;
	}

	public StringBuffer branchAdd(String branch) {
		List<String> commands = new ArrayList<String>();
		commands.add("branch");
		commands.add(branch);
		StringBuffer result = process(commands);
		return result;
	}

	public StringBuffer branch() {
		List<String> commands = new ArrayList<String>();
		commands.add("branch");
		commands.add("-v");
		StringBuffer result = process(commands);
		return result;
	}

	public StringBuffer push(String remote, String branch, String remoteBranch, String username, String password) {
		if (!StringUtil.isEmpty(username) && !StringUtil.isEmpty(username)) {
			login(username, null, password);
		}
		List<String> commands = new ArrayList<String>();
		commands.add("push");
		commands.add(remote);
		commands.add(branch + ":" + remoteBranch);
		StringBuffer result = process(commands);
		if (!StringUtil.isEmpty(username) && !StringUtil.isEmpty(username)) {
			// logout();
		}
		return result;
	}

	public StringBuffer fetch(String remote) {
		List<String> commands = new ArrayList<String>();
		commands.add("fetch");
		if (!StringUtil.isEmpty(remote)) {
			commands.add(remote);
		}
		StringBuffer result = process(commands);
		return result;
	}

	public StringBuffer login(String name, String email, String password) {

		if (name != null) {
			configGlobal("user.name", name);
		}
		if (email != null) {
			configGlobal("user.email", email);
		}
		if (password != null) {
			configGlobal("user.password", password);
		}

		StringBuffer result = configGlobal("credential.helper", "store");
		return result;
	}

	public StringBuffer logout() {
		StringBuffer result = configGlobal("user.name", "");
		result = configGlobal("user.email", "");
		result = configGlobal("user.password", "");
		return result;
	}

	public StringBuffer configGlobal(String name, String value) {
		List<String> commands = new ArrayList<String>();
		commands.add("config");
		commands.add("--global");
		commands.add(name);
		commands.add("\"" + value + "\"");
		StringBuffer result = process(commands);
		return result;
	}

	public StringBuffer pull(String remote, String username, String password) {
		if (!StringUtil.isEmpty(username) && !StringUtil.isEmpty(username)) {
			login(username, null, password);
		}
		List<String> commands = new ArrayList<String>();
		commands.add("pull");
		if (!StringUtil.isEmpty(remote)) {
			commands.add(remote);
		}
		StringBuffer result = process(commands);

		if (!StringUtil.isEmpty(username) && !StringUtil.isEmpty(username)) {
			logout();
		}
		return result;
	}

	public StringBuffer remoteAdd(String remote, String url) {
		List<String> commands = new ArrayList<String>();
		commands.add("remote");
		commands.add("add");
		commands.add(remote);
		commands.add(url);
		StringBuffer result = process(commands);
		return result;
	}

	public StringBuffer remoteShow(String remote) {
		List<String> commands = new ArrayList<String>();
		commands.add("remote");
		commands.add("show");
		if (!StringUtil.isEmpty(remote)) {
			commands.add(remote);
		}
		StringBuffer result = process(commands);
		return result;
	}

	public StringBuffer remote() {
		List<String> commands = new ArrayList<String>();
		commands.add("remote");
		commands.add("-v");
		StringBuffer result = process(commands);
		return result;
	}

	public StringBuffer remoteRemove(String remote) {
		List<String> commands = new ArrayList<String>();
		commands.add("remote");
		commands.add("remove");
		commands.add(remote);
		StringBuffer result = process(commands);
		return result;
	}

	public StringBuffer init() {
		List<String> commands = new ArrayList<String>();
		commands.add("init");
		StringBuffer result = process(commands);
		StringBuffer branch = branch();
		if (branch.indexOf("master") < 0) {
			File file = new File(root, "" + System.currentTimeMillis());
			try {
				file.createNewFile();
				add(file.getName());
				commit("initial commit.");
				reset();

			} catch (Exception e) {
				e.printStackTrace();
			}
			file.delete();
		}
		return result;
	}

	private StringBuffer process(List<String> commands) {
		StringBuffer result = new StringBuffer();
		try {
			List<String> list = new ArrayList<String>();
			if (IDEConstant.IS_OS_WINDOW) {
				list.add("cmd");
				list.add("/C");
			} else if (IDEConstant.IS_OS_LINUX) {
				list.add("/bin/sh");
				list.add("-c");
			}
			if (StringUtil.isEmpty(git_home)) {
				list.add("git");
			} else {
				list.add(git_home + "/bin/git");
			}
			list.addAll(commands);
			String[] cmds = new String[list.size()];
			cmds = list.toArray(cmds);
			System.out.println(JSON.toJSONString(cmds));
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(cmds, null, root);

			// 取得命令结果的输出流
			append(result, process.getInputStream());
			append(result, process.getErrorStream());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		System.out.println(result);

		return result;
	}

	private void append(StringBuffer buffer, InputStream stream) {
		BufferedReader reader = null;
		try {
			// 用缓冲器读行
			reader = new BufferedReader(new InputStreamReader(stream));
			String line = null;
			// 直到读完为止
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
				buffer.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
				}
			}
		}
	}

}
