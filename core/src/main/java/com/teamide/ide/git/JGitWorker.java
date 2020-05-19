package com.teamide.ide.git;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.api.CherryPickResult;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.ReflogEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class JGitWorker {

	private final File dirFile;

	private Git git;

	public JGitWorker(String dir) {

		this.dirFile = new File(dir);
	}

	public Git init() throws Exception {

		try {
			git = open();
		} catch (RepositoryNotFoundException e) {
			git = JGitUtil.init(dirFile);
			git = open();
		}
		return git;
	}

	public void close() throws Exception {

		if (git != null) {
			JGitUtil.close(git);
			git = null;
		}
	}

	public Git open() throws Exception {

		if (git == null) {
			git = JGitUtil.open(dirFile);
		}
		return git;
	}

	public Status status() throws Exception {

		try {
			return JGitUtil.status(open());
		} finally {
			close();
		}
	}

	public Repository getRepository() throws Exception {

		try {
			return open().getRepository();
		} finally {
			close();
		}
	}

	public StoredConfig getConfig() throws Exception {

		try {
			return open().getRepository().getConfig();
		} finally {
			close();
		}
	}

	public Git clone(String uri, String branch, String remote, UsernamePasswordCredentialsProvider credentialsProvider)
			throws Exception {

		try {
			git = JGitUtil.cloneRepository(dirFile, uri, branch, remote, credentialsProvider);
			return git;
		} finally {
			close();
		}
	}

	public FetchResult fetch(String remote, UsernamePasswordCredentialsProvider credentialsProvider) throws Exception {

		try {
			return JGitUtil.fetch(git, remote, credentialsProvider);
		} finally {
			close();
		}
	}

	public RemoteConfig remoteSetUrl(String name, String uri) throws Exception {

		try {
			return JGitUtil.remoteSetUrl(open(), name, uri);
		} finally {
			close();
		}
	}

	public RemoteConfig remoteAdd(String name, String uri) throws Exception {

		try {
			return JGitUtil.remoteAdd(open(), name, uri);
		} finally {
			close();
		}
	}

	public void setSslVerifyFalse() throws Exception {

		try {
			StoredConfig config = open().getRepository().getConfig();
			config.setBoolean("http", null, "sslVerify", false);
			config.save();
		} finally {
			close();
		}
	}

	public RemoteConfig remoteRemove(String name) throws Exception {

		try {
			return JGitUtil.remoteRemove(open(), name);
		} finally {
			close();
		}
	}

	public List<RemoteConfig> remoteList() throws Exception {

		try {
			return JGitUtil.remoteList(open());
		} finally {
			close();
		}
	}

	public Ref branchCreate(String name, String startPoint) throws Exception {

		try {
			return JGitUtil.branchCreate(open(), name, startPoint);
		} finally {
			close();
		}
	}

	public List<String> branchDelete(String... branchnames) throws Exception {

		try {
			return JGitUtil.branchDelete(open(), branchnames);
		} finally {
			close();
		}
	}

	public Ref branchRename(String oldName, String newName) throws Exception {

		try {
			return JGitUtil.branchRename(open(), oldName, newName);
		} finally {
			close();
		}
	}

	public List<Ref> branchList(ListMode mode) throws Exception {

		try {
			return JGitUtil.branchList(open(), mode);
		} finally {
			close();
		}
	}

	public CherryPickResult cherryPick() throws Exception {

		try {
			return JGitUtil.cherryPick(open());
		} finally {
			close();
		}
	}

	public Ref checkout(String branch, String startPoint) throws Exception {

		try {
			return JGitUtil.checkout(open(), branch, startPoint);
		} finally {
			close();
		}
	}

	public DirCache add(String filepattern) throws Exception {

		try {
			return JGitUtil.add(open(), filepattern);
		} finally {
			close();
		}
	}

	public Collection<ReflogEntry> refLog() throws Exception {

		try {
			return JGitUtil.refLog(open());
		} finally {
			close();
		}
	}

	public DirCache rm(String filepattern) throws Exception {

		try {
			return JGitUtil.rm(open(), filepattern);
		} finally {
			close();
		}
	}

	public Ref reset(String filepattern) throws Exception {

		try {
			return JGitUtil.reset(open(), filepattern);
		} finally {
			close();
		}
	}

	public Ref checkout(String path) throws Exception {

		try {
			return JGitUtil.checkout(open(), path);
		} finally {
			close();
		}
	}

	public Ref revert(List<String> path) throws Exception {

		try {
			return JGitUtil.checkout(open(), path);
		} finally {
			close();
		}
	}

	public RevCommit commit(String message) throws Exception {

		try {
			return JGitUtil.commit(open(), message);
		} finally {
			close();
		}
	}

	public PullResult pull(String remote, String remoteBranchName,
			UsernamePasswordCredentialsProvider credentialsProvider) throws Exception {

		try {
			return JGitUtil.pull(open(), remote, remoteBranchName, credentialsProvider);
		} finally {
			close();
		}
	}

	public Iterable<PushResult> push(String remote, String branchName, String remoteBranchName,
			UsernamePasswordCredentialsProvider credentialsProvider) throws Exception {

		try {
			return JGitUtil.push(open(), remote, branchName, remoteBranchName, credentialsProvider);
		} finally {
			close();
		}
	}

	public Status status(String path) throws Exception {

		try {
			return JGitUtil.status(open(), path);
		} finally {
			close();
		}
	}

	public Iterable<RevCommit> log(int maxCount) throws Exception {

		try {
			return JGitUtil.log(open(), maxCount);
		} finally {
			close();
		}
	}

	public List<DiffEntry> diff(String remote, String remoteBranchName) throws Exception {

		try {
			return JGitUtil.diff(open(), remote, remoteBranchName);
		} finally {
			close();
		}
	}
}
