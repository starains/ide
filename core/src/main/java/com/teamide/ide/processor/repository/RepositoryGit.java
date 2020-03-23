package com.teamide.ide.processor.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.CherryPickResult;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.ReflogEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.teamide.ide.enums.GitWorkStatus;
import com.teamide.ide.enums.OptionType;
import com.teamide.ide.git.JGitWorker;
import com.teamide.ide.processor.param.RepositoryProcessorParam;
import com.teamide.util.StringUtil;

public class RepositoryGit extends RepositoryBase {

	public static Map<String, GitWorkStatus> WORK_STATUS_CACHE = new HashMap<String, GitWorkStatus>();

	public static Map<String, String> WORK_MESSAGE_CACHE = new HashMap<String, String>();

	public RepositoryGit(RepositoryProcessorParam param) {

		super(param);
		worker = new JGitWorker(this.param.getSourceFolder().getAbsolutePath());
	}

	private final JGitWorker worker;

	public String getKey() {
		return this.param.getSpaceid() + "-" + this.param.getBranch();
	}

	public GitWorkStatus getGitWorkStatus() {
		return WORK_STATUS_CACHE.get(getKey());
	}

	public void setGitWorkStatus(GitWorkStatus gitWorkStatus) {
		WORK_STATUS_CACHE.put(getKey(), gitWorkStatus);
	}

	public String getGitWorkMessage() {
		return WORK_MESSAGE_CACHE.get(getKey());
	}

	public void setGitWorkMessage(String message) {
		WORK_MESSAGE_CACHE.put(getKey(), message);
	}

	public void checkWorkStatus() throws Exception {
		if (getGitWorkStatus() != null) {
			if (getGitWorkStatus() == GitWorkStatus.CHECKOUTING) {
				throw new Exception("git检出中，请稍后再试...");
			}
			if (getGitWorkStatus() == GitWorkStatus.PULLING) {
				throw new Exception("git拉取中，请稍后再试...");
			}
			if (getGitWorkStatus() == GitWorkStatus.PUSHING) {
				throw new Exception("git推送中，请稍后再试...");
			}
		}
	}

	public JSONObject loadCertificateOption() throws Exception {

		return this.param.getOption(null, null, OptionType.GIT_CERTIFICATE);
	}

	public void saveCertificateOption(String username, String password) throws Exception {
		JSONObject json = new JSONObject();
		json.put("username", username);
		json.put("password", password);

		this.param.saveOption(null, null, OptionType.GIT_CERTIFICATE, json);
	}

	public JSONObject load() throws Exception {

		this.param.getLog().info("git load");

		JSONObject result = new JSONObject();
		result.put("certificate", loadCertificateOption());
		result.put("option", this.param.getOption(null, null, OptionType.GIT));
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		List<RemoteConfig> remoteList = worker.remoteList();
		List<Ref> branchList = worker.branchList();

		Status status = worker.status();
		result.put("remoteList", toJSONByRemoteConfigs(remoteList));
		result.put("branchList", toJSONByRefs(branchList));
		try {

			// result.put("reflogs", toReflogs(worker.refLog()));
			// result.put("cherryPick", toCherryPick(worker.cherryPick()));
			result.put("status", toStatus(status));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONArray toReflogs(Collection<ReflogEntry> refs) {

		// for (ReflogEntry ref : refs) {
		// System.out.println(ref.getComment());
		// }
		return (JSONArray) JSON.toJSON(refs);
	}

	public JSONObject toCherryPick(CherryPickResult cherryPickResult) {

		JSONObject json = new JSONObject();
		json.put("refs", toJSONByRefs(cherryPickResult.getCherryPickedRefs()));
		json.put("failingPaths", cherryPickResult.getFailingPaths());
		json.put("status", cherryPickResult.getStatus());
		return json;
	}

	public JSONObject toStatus(Status status) {

		JSONObject json = (JSONObject) JSON.toJSON(status);
		json.put("hasUncommittedChanges", status.hasUncommittedChanges());
		json.put("uncommittedChanges", status.getUncommittedChanges());
		return json;
	}

	public JSONArray toJSONByRemoteConfigs(List<RemoteConfig> remoteList) {
		JSONArray res = new JSONArray();
		for (RemoteConfig remoteConfig : remoteList) {
			res.add(toJSON(remoteConfig));
		}
		return res;
	}

	public JSONObject toJSON(RemoteConfig remoteConfig) {
		JSONObject json = new JSONObject();
		json.put("name", remoteConfig.getName());
		return json;
	}

	public JSONArray toJSONByRefs(List<Ref> branchList) {

		JSONArray res = new JSONArray();
		for (Ref ref : branchList) {
			res.add(toJSON(ref));
		}
		return res;
	}

	public JSONObject toJSON(Ref ref) {
		JSONObject json = new JSONObject();
		if (ref != null) {
			json.put("name", ref.getName());
		}
		return json;
	}

	public JSONObject init() throws Exception {

		JSONObject result = new JSONObject();

		worker.init();
		return result;
	}

	public boolean findGit() throws Exception {

		try {
			worker.open();
			return true;
		} catch (RepositoryNotFoundException e) {
			return false;
		}
	}

	public JSONObject clone(String uri, String branch, String remote, String username, String password)
			throws Exception {

		this.param.getLog().info("git clone");

		JSONObject result = new JSONObject();
		worker.clone(uri, branch, remote, getCredentialsProvider(username, password));
		return result;
	}

	public JSONObject remoteSetUrl(String name, String uri) throws Exception {

		this.param.getLog().info("git remote set url, name:" + name + ", url:" + uri);
		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		RemoteConfig remoteConfig = worker.remoteSetUrl(name, uri);
		result.put("remoteConfig", toJSON(remoteConfig));
		return result;
	}

	public JSONObject remoteAdd(String name, String uri) throws Exception {

		this.param.getLog().info("git remote add, name:" + name + ", url:" + uri);

		JSONObject result = new JSONObject();
		if (!findGit()) {
			worker.init();
		}
		result.put("findGit", true);

		RemoteConfig remoteConfig = worker.remoteAdd(name, uri);
		result.put("remoteConfig", toJSON(remoteConfig));
		return result;
	}

	public JSONObject remoteRemove(String name) throws Exception {

		this.param.getLog().info("git remote remove, name:" + name);

		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		RemoteConfig remoteConfig = worker.remoteRemove(name);
		result.put("remoteConfig", toJSON(remoteConfig));
		return result;
	}

	public JSONObject remoteList() throws Exception {

		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		List<RemoteConfig> remoteList = worker.remoteList();
		result.put("remoteList", toJSONByRemoteConfigs(remoteList));
		return result;
	}

	public JSONObject branchCreate(String name, String startPoint) throws Exception {

		this.param.getLog().info("git branch create, name:" + name + ", startPoint:" + startPoint);
		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		Ref ref = worker.branchCreate(name, startPoint);
		result.put("ref", toJSON(ref));
		return result;
	}

	public JSONObject branchDelete(String... branchnames) throws Exception {

		this.param.getLog().info("git branch delete, names:" + branchnames);

		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		List<String> list = worker.branchDelete(branchnames);
		result.put("list", list);
		return result;
	}

	public JSONObject branchRename(String oldName, String newName) throws Exception {

		this.param.getLog().info("git branch rename, oldName:" + oldName + ", newName:" + newName);

		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		Ref ref = worker.branchRename(oldName, newName);
		result.put("ref", toJSON(ref));
		return result;
	}

	public JSONObject branchList() throws Exception {

		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		List<Ref> branchList = worker.branchList();
		result.put("branchList", toJSONByRefs(branchList));
		return result;
	}

	public JSONObject checkout(String branch, String startPoint) throws Exception {

		this.param.getLog().info("git checkout, branch:" + branch + ", startPoint:" + startPoint);

		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		Ref ref = worker.checkout(branch, startPoint);
		result.put("ref", toJSON(ref));
		return result;
	}

	public JSONObject add(String filepattern) throws Exception {

		this.param.getLog().info("git add, filepattern:" + filepattern);

		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		DirCache dirCache = worker.add(filepattern);
		try {
			result.put("dirCache", toJSON(dirCache));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONObject rm(String filepattern) throws Exception {

		this.param.getLog().info("git rm, filepattern:" + filepattern);

		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		DirCache dirCache = worker.rm(filepattern);
		try {
			result.put("dirCache", toJSON(dirCache));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONObject reset(String filepattern) throws Exception {

		this.param.getLog().info("git reset, filepattern:" + filepattern);

		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		Ref ref = worker.reset(filepattern);
		try {
			result.put("ref", toJSON(ref));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONObject revert(List<String> paths) throws Exception {
		this.param.getLog().info("git revert, paths:" + paths);

		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		Ref ref = worker.revert(paths);
		try {
			result.put("ref", toJSON(ref));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONObject checkout(String path) throws Exception {
		this.param.getLog().info("git checkout,  path:" + path);

		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		Ref ref = worker.checkout(path);
		try {
			result.put("ref", toJSON(ref));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONObject toJSON(DirCache dirCache) {
		JSONObject json = new JSONObject();
		return json;
	}

	public JSONObject commit(String message) throws Exception {
		this.param.getLog().info("git commit,  message:" + message);

		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		RevCommit revCommit = worker.commit(message);
		try {
			result.put("revCommit", toJSON(revCommit));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public JSONObject toJSON(RevCommit revCommit) {
		JSONObject json = new JSONObject();
		return json;
	}

	public JSONObject pull(String remote, String remoteBranchName, String username, String password) throws Exception {

		this.param.getLog().info("git pull,  remote:" + remote + ",  remoteBranchName:" + remoteBranchName);
		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		checkWorkStatus();
		setGitWorkStatus(GitWorkStatus.PULLING);
		new Thread() {

			@Override
			public void run() {
				try {
					worker.pull(remote, remoteBranchName, getCredentialsProvider(username, password));
					setGitWorkStatus(GitWorkStatus.PULLED);
					setGitWorkMessage(null);
				} catch (Exception e) {
					setGitWorkStatus(GitWorkStatus.PULLERR);
					setGitWorkMessage(e.getMessage());
				}
			}

		}.start();

		return result;
	}

	public JSONObject push(JSONArray paths, String message, String remote, String branchName, String remoteBranchName,
			String username, String password) throws Exception {
		this.param.getLog().info("git push,  remote:" + remote + ",  remoteBranchName:" + remoteBranchName);

		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		checkWorkStatus();
		setGitWorkStatus(GitWorkStatus.PUSHING);
		new Thread() {

			@Override
			public void run() {
				try {
					// 检查最新版本
					FetchResult fetchResult = worker.fetch(remote, getCredentialsProvider(username, password));
					Collection<TrackingRefUpdate> updates = fetchResult.getTrackingRefUpdates();
					if (updates != null && updates.size() > 0) {
						throw new Exception("有最新代码需要更新，请先更新代码。");
					}
					// List<DiffEntry> diffs = worker.diff(remote,
					// remoteBranchName);
					// if (diffs != null && diffs.size() > 0) {
					// for (DiffEntry diff : diffs) {
					// System.out.println(diff);
					// }
					// throw new Exception("有最新代码需要更新，请先更新代码。");
					// }

					add(paths);

					commit(message);

					worker.push(remote, branchName, remoteBranchName, getCredentialsProvider(username, password));
					setGitWorkStatus(GitWorkStatus.PUSHED);
					setGitWorkMessage(null);
				} catch (Exception e) {
					setGitWorkStatus(GitWorkStatus.PUSHERR);
					setGitWorkMessage(e.getMessage());
				}
			}

		}.start();

		return result;
	}

	public JSONObject status(String path) throws Exception {

		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);
		Status status = worker.status(path);
		try {
			result.put("status", toStatus(status));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void add(JSONArray paths) throws Exception {
		JSONArray plusPaths = findPlusPaths();

		for (int i = 0; i < paths.size(); i++) {
			JSONObject one = paths.getJSONObject(i);
			boolean find = false;
			for (int n = 0; n < plusPaths.size(); n++) {
				JSONObject plusPath = plusPaths.getJSONObject(n);
				if (plusPath.getString("path").equals(one.getString("path"))) {
					find = true;
				}
			}
			if (!find) {
				if (one.getString("status").equals("missing")) {
					rm(one.getString("path"));
				} else {
					add(one.getString("path"));
				}
			}
		}
		for (int n = 0; n < plusPaths.size(); n++) {
			JSONObject plusPath = plusPaths.getJSONObject(n);
			boolean find = false;
			for (int i = 0; i < paths.size(); i++) {
				JSONObject one = paths.getJSONObject(i);
				if (plusPath.getString("path").equals(one.getString("path"))) {
					find = true;
				}
			}
			if (!find) {
				reset(plusPath.getString("path"));
			}
		}
	}

	public JSONArray findPlusPaths() throws Exception {
		JSONArray list = new JSONArray();
		JSONObject status = status(null).getJSONObject("status");
		if (status == null) {
			return list;
		}
		JSONArray array = status.getJSONArray("added");

		if (array != null) {
			List<String> added = array.toJavaList(String.class);
			for (String add : added) {
				JSONObject one = new JSONObject();
				one.put("path", add);
				one.put("status", "added");
				list.add(one);
			}
		}
		array = status.getJSONArray("changed");

		if (array != null) {
			List<String> changed = array.toJavaList(String.class);
			for (String change : changed) {
				JSONObject one = new JSONObject();
				one.put("path", change);
				one.put("status", "changed");
				list.add(one);
			}
		}
		array = status.getJSONArray("removed");

		if (array != null) {
			List<String> removed = array.toJavaList(String.class);
			for (String remove : removed) {
				JSONObject one = new JSONObject();
				one.put("path", remove);
				one.put("status", "removed");
				list.add(one);
			}
		}
		return list;
	}

	public JSONObject log(int maxCount) throws Exception {

		JSONObject result = new JSONObject();
		if (!findGit()) {
			result.put("findGit", false);
			return result;
		}
		result.put("findGit", true);

		Iterable<RevCommit> revCommits = worker.log(maxCount);
		try {
			result.put("revCommits", JSON.toJSON(revCommits));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public UsernamePasswordCredentialsProvider getCredentialsProvider(String username, String password)
			throws Exception {

		saveCertificateOption(username, password);
		if (StringUtil.isNotEmpty(username) && StringUtil.isNotEmpty(password)) {
			return new UsernamePasswordCredentialsProvider(username, password);
		}
		return null;
	}

}
