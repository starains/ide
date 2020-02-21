package com.teamide.ide.protect.git;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CherryPickCommand;
import org.eclipse.jgit.api.CherryPickResult;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.DeleteBranchCommand;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.ReflogCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.RemoteListCommand;
import org.eclipse.jgit.api.RemoteRemoveCommand;
import org.eclipse.jgit.api.RemoteSetUrlCommand;
import org.eclipse.jgit.api.RenameBranchCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.ReflogEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import com.teamide.util.StringUtil;

/**
 * Git操作工具类
 */
public class JGitUtil {

	/**
	 * 打开
	 * 
	 * @param dir
	 * @return
	 * @throws GitAPIException
	 */
	public static Git open(File dir) throws IOException {

		Git git = Git.open(dir);
		return git;
	}

	/**
	 * 创建一个新的仓库
	 * 
	 * @param dir
	 * @return
	 * @throws GitAPIException
	 */
	public static Git init(File dir) throws GitAPIException {

		InitCommand command = Git.init();
		command.setDirectory(dir);
		return command.call();
	}

	/**
	 * 克隆远程 JGit 仓库到本地目录
	 * 
	 * @param uri
	 * @param dir
	 * @return
	 * @throws GitAPIException
	 */
	public static Git cloneRepository(File dir, String uri, String branch, String remote,
			CredentialsProvider credentialsProvider) throws GitAPIException {

		CloneCommand command = Git.cloneRepository();
		command.setURI(uri).setDirectory(dir);
		if (StringUtil.isNotEmpty(branch)) {
			command.setBranch(branch);
		}
		if (StringUtil.isNotEmpty(remote)) {
			command.setRemote(remote);
		}
		if (credentialsProvider != null) {
			command.setCredentialsProvider(credentialsProvider);
		}
		Git git = command.call();
		return git;
	}

	public static void close(Git git) {

		git.close();
	}

	/**
	 * 添加文件
	 * 
	 * @param git
	 * @param filepattern
	 *            路径必须相对于工作目录的根 传一个‘.’将在工作目录中添加所有文件
	 * @return
	 * @throws GitAPIException
	 */
	public static DirCache add(Git git, String filepattern) throws GitAPIException {

		AddCommand command = git.add();
		command.addFilepattern(filepattern);
		return command.call();
	}

	/**
	 * 删除文件
	 * 
	 * @param git
	 * @param filepattern
	 *            路径必须相对于工作目录的根 传一个‘.’将在工作目录中添加所有文件
	 * @return
	 * @throws GitAPIException
	 */
	public static DirCache rm(Git git, String filepattern) throws GitAPIException {

		RmCommand command = git.rm();
		command.addFilepattern(filepattern);
		return command.call();
	}

	/**
	 * 变化存入仓库
	 * 
	 * @param git
	 * @param message
	 * @return
	 * @throws GitAPIException
	 */
	public static RevCommit commit(Git git, String message) throws GitAPIException {

		CommitCommand commit = git.commit();
		commit.setMessage(message);
		return commit.call();
	}

	/**
	 * 查看已经提交 但是未传送到远程代码库的提交描述/说明
	 * 
	 * @param git
	 * @return
	 * @throws GitAPIException
	 */
	public static CherryPickResult cherryPick(Git git) throws GitAPIException {

		CherryPickCommand commit = git.cherryPick();
		// commit.setNoCommit(true);
		return commit.call();
	}

	/**
	 * 状态
	 * 
	 * @param git
	 * @return
	 * @throws GitAPIException
	 */
	public static Status status(Git git) throws GitAPIException {

		return status(git, null);
	}

	/**
	 * 显示某些文件状态
	 * 
	 * @param git
	 * @param path
	 * @return
	 * @throws GitAPIException
	 */
	public static Status status(Git git, String path) throws GitAPIException {

		StatusCommand command = git.status();
		if (StringUtil.isNotEmpty(path)) {
			command.addPath(path);
		}
		return command.call();
	}

	/**
	 * 目前的 HEAD 中得到的提交
	 * 
	 * @param git
	 * @return
	 * @throws GitAPIException
	 */
	public static List<DiffEntry> diff(Git git) throws GitAPIException {

		DiffCommand command = git.diff();
		return command.call();
	}

	/**
	 * 目前的 HEAD 中得到的提交
	 * 
	 * @param git
	 * @return
	 * @throws GitAPIException
	 */
	public static Iterable<RevCommit> log(Git git, int maxCount) throws GitAPIException {

		LogCommand command = git.log();
		command.setMaxCount(maxCount);
		return command.call();
	}

	public static Collection<ReflogEntry> refLog(Git git) throws GitAPIException {

		ReflogCommand command = git.reflog();
		return command.call();
	}

	public static RemoteConfig remoteSetUrl(Git git, String name, String uri) throws Exception {

		URIish urIish = new URIish(uri);
		RemoteSetUrlCommand command = git.remoteSetUrl();
		command.setRemoteName(name);
		command.setRemoteUri(urIish);
		return command.call();
	}

	public static RemoteConfig remoteAdd(Git git, String name, String uri) throws Exception {

		URIish urIish = new URIish(uri);
		RemoteAddCommand command = git.remoteAdd();
		command.setName(name);
		command.setUri(urIish);

		return command.call();
	}

	public static RemoteConfig remoteRemove(Git git, String name) throws Exception {

		RemoteRemoveCommand command = git.remoteRemove();
		command.setRemoteName(name);

		return command.call();
	}

	public static List<RemoteConfig> remoteList(Git git) throws Exception {

		RemoteListCommand command = git.remoteList();
		return command.call();
	}

	public static Ref branchCreate(Git git, String name, String startPoint) throws Exception {

		CreateBranchCommand command = git.branchCreate();
		command.setName(name);
		command.setStartPoint(startPoint);
		return command.call();
	}

	public static Ref branchRename(Git git, String oldName, String newName) throws Exception {

		RenameBranchCommand command = git.branchRename();
		command.setNewName(newName);
		command.setOldName(oldName);
		return command.call();
	}

	public static List<String> branchDelete(Git git, String... branchnames) throws Exception {

		DeleteBranchCommand command = git.branchDelete();
		command.setBranchNames(branchnames);
		return command.call();
	}

	public static List<Ref> branchList(Git git) throws Exception {

		ListBranchCommand command = git.branchList();
		command.setListMode(ListMode.ALL);
		return command.call();
	}

	public static Ref checkout(Git git, String branch, String startPoint) throws Exception {

		CheckoutCommand command = git.checkout();
		command.setName(branch);
		if (StringUtil.isNotEmpty(startPoint)) {
			command.setCreateBranch(true);
			command.setStartPoint(startPoint);
		}
		return command.call();
	}

	public static Ref reset(Git git, String path) throws Exception {

		ResetCommand command = git.reset();
		// command.setMode(ResetType.HARD);
		command.addPath(path);
		return command.call();
	}

	/**
	 * 还原
	 * 
	 * @param git
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static Ref checkout(Git git, String path) throws Exception {

		CheckoutCommand command = git.checkout();
		if (path.equals(".")) {
			command.setAllPaths(true);
		} else {
			command.addPath(path);
		}
		return command.call();
	}

	/**
	 * 还原
	 * 
	 * @param git
	 * @param paths
	 * @return
	 * @throws Exception
	 */
	public static Ref checkout(Git git, List<String> paths) throws Exception {

		CheckoutCommand command = git.checkout();
		command.addPaths(paths);
		return command.call();
	}

	public static Iterable<PushResult> push(Git git, String remote, String branchName, String remoteBranchName,
			CredentialsProvider credentialsProvider) throws Exception {
		PushCommand command = git.push();
		if (StringUtil.isNotEmpty(remote) && StringUtil.isNotEmpty(branchName)
				&& StringUtil.isNotEmpty(remoteBranchName)) {
			command.add(remote);
			command.add(branchName + ":" + remoteBranchName);
		}
		if (credentialsProvider != null) {
			command.setCredentialsProvider(credentialsProvider);
		}
		return command.call();
	}

	public static PullResult pull(Git git, String remote, String remoteBranchName,
			CredentialsProvider credentialsProvider) throws Exception {

		PullCommand command = git.pull();
		if (StringUtil.isNotEmpty(remote)) {
			command.setRemote(remote);
		}
		if (StringUtil.isNotEmpty(remoteBranchName)) {
			command.setRemoteBranchName(remoteBranchName);
		}
		if (credentialsProvider != null) {
			command.setCredentialsProvider(credentialsProvider);
		}
		return command.call();
	}

}