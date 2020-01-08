package com.teamide.protect.ide.processor;

import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.teamide.util.StringUtil;
import com.teamide.db.bean.PageSqlParam;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.bean.SpaceEventBean;
import com.teamide.ide.bean.SpaceStarBean;
import com.teamide.ide.bean.SpaceTeamBean;
import com.teamide.ide.bean.UserBean;
import com.teamide.ide.client.Client;
import com.teamide.ide.enums.SpacePermission;
import com.teamide.ide.service.ISpaceEventService;
import com.teamide.ide.service.ISpaceService;
import com.teamide.ide.service.ISpaceTeamService;
import com.teamide.ide.service.IUserService;
import com.teamide.protect.ide.engine.EngineCache;
import com.teamide.protect.ide.engine.EngineSession;
import com.teamide.protect.ide.handler.SpaceHandler;
import com.teamide.protect.ide.processor.enums.MessageLevel;
import com.teamide.protect.ide.processor.enums.SpaceModelType;
import com.teamide.protect.ide.processor.enums.SpaceProcessorType;
import com.teamide.protect.ide.processor.param.SpaceProcessorParam;
import com.teamide.protect.ide.service.SpaceEventService;
import com.teamide.protect.ide.service.SpaceService;
import com.teamide.protect.ide.service.SpaceStarService;
import com.teamide.protect.ide.service.SpaceTeamService;
import com.teamide.protect.ide.service.UserService;

public class SpaceProcessor extends Processor {

	protected final SpaceProcessorParam param;

	public SpaceProcessor(EngineSession session, SpaceProcessorParam param) {
		super(session, param);
		this.param = param;
	}

	protected void process(String messageID, String type, JSONObject data) throws Exception {
		SpaceProcessorType processorType = SpaceProcessorType.get(type);
		if (processorType == null) {
			super.process(messageID, type, data);
			return;
		}
		SpaceEventBean spaceEventBean = new SpaceEventBean();
		spaceEventBean.setType(processorType.getValue());
		spaceEventBean.setName(processorType.getText());
		spaceEventBean.setSpaceid(param.getSpaceid());

		Object value = null;
		switch (processorType) {
		case SPACE_CREATE:
			SpaceService spaceService = new SpaceService();
			SpaceBean space = data.toJavaObject(SpaceBean.class);
			value = spaceService.insert(param.getClient(), space);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			outMessage(MessageLevel.SUCCESS, "新增成功！");
			break;
		case SPACE_UPDATE:
			spaceService = new SpaceService();
			space = data.toJavaObject(SpaceBean.class);
			value = spaceService.update(param.getClient(), space);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			outMessage(MessageLevel.SUCCESS, "修改成功！");
			break;
		case SPACE_DELETE:
			value = data.toJavaObject(SpaceBean.class);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			outMessage(MessageLevel.SUCCESS, "删除成功！");
			break;

		case SPACE_TEAM_INSERT:
			SpaceTeamBean spaceTeamBean = data.toJavaObject(SpaceTeamBean.class);
			SpaceTeamService spaceTeamService = new SpaceTeamService();
			value = spaceTeamService.insert(param.getClient(), spaceTeamBean);

			spaceEventBean.set(data);
			if (value != null) {
				spaceEventBean.setSpaceid(((SpaceTeamBean) value).getSpaceid());
			}
			appendEvent(spaceEventBean);

			outMessage(MessageLevel.SUCCESS, "添加成功！");
			break;
		case SPACE_TEAM_UPDATE:
			spaceTeamBean = data.toJavaObject(SpaceTeamBean.class);
			spaceTeamService = new SpaceTeamService();
			value = spaceTeamService.update(param.getClient(), spaceTeamBean);

			spaceEventBean.set(data);
			if (value != null) {
				spaceEventBean.setSpaceid(((SpaceTeamBean) value).getSpaceid());
			}
			appendEvent(spaceEventBean);

			outMessage(MessageLevel.SUCCESS, "修改成功！");
			break;
		case SPACE_TEAM_DELETE:
			spaceTeamBean = data.toJavaObject(SpaceTeamBean.class);
			spaceTeamService = new SpaceTeamService();
			value = spaceTeamService.delete(param.getClient(), spaceTeamBean);

			spaceEventBean.set(data);
			if (value != null) {
				spaceEventBean.setSpaceid(((SpaceTeamBean) value).getSpaceid());
			}
			appendEvent(spaceEventBean);

			outMessage(MessageLevel.SUCCESS, "删除成功！");
			break;

		case SPACE_STAR_INSERT:
			SpaceStarBean spaceStarBean = data.toJavaObject(SpaceStarBean.class);
			SpaceStarService spaceStarService = new SpaceStarService();
			value = spaceStarService.insert(param.getClient(), spaceStarBean);

			spaceEventBean.set(data);
			if (value != null) {
				spaceEventBean.setSpaceid(((SpaceStarBean) value).getSpaceid());
			}
			appendEvent(spaceEventBean);

			outMessage(MessageLevel.SUCCESS, "添加成功！");
			break;
		case SPACE_STAR_UPDATE:
			spaceStarBean = data.toJavaObject(SpaceStarBean.class);
			spaceStarService = new SpaceStarService();
			value = spaceStarService.update(param.getClient(), spaceStarBean);

			spaceEventBean.set(data);
			if (value != null) {
				spaceEventBean.setSpaceid(((SpaceStarBean) value).getSpaceid());
			}
			appendEvent(spaceEventBean);

			outMessage(MessageLevel.SUCCESS, "修改成功！");
			break;
		case SPACE_STAR_DELETE:
			spaceStarBean = data.toJavaObject(SpaceStarBean.class);
			spaceStarService = new SpaceStarService();
			value = spaceStarService.delete(param.getClient(), spaceStarBean);

			spaceEventBean.set(data);
			if (value != null) {
				spaceEventBean.setSpaceid(((SpaceStarBean) value).getSpaceid());
			}
			appendEvent(spaceEventBean);

			outMessage(MessageLevel.SUCCESS, "删除成功！");
			break;

		}
		out(messageID, processorType.getValue(), value);
	}

	public void onData(String messageID, String model, JSONObject data) throws Exception {
		SpaceModelType modelType = SpaceModelType.get(model);
		if (modelType == null) {
			super.onData(messageID, model, data);
			return;
		}
		onData(messageID, modelType, data);
	}

	public void onData(String messageID, SpaceModelType modelType, JSONObject data) throws Exception {
		if (modelType == null) {
			return;
		}
		Object value = null;
		Client client = this.param.getClient();
		int pageindex = data.getIntValue("pageindex");
		int pagesize = data.getIntValue("pagesize");
		JSONObject param = new JSONObject();
		switch (modelType) {
		case SPACE:
			JSONObject space_format = SpaceHandler.getFormat(this.param.getSpace());
			SpacePermission permission = SpaceHandler.getPermission(this.param.getSpace(), client);
			space_format.put("permission", permission);
			value = space_format;
			break;
		case PARENTS:
			ISpaceService spaceService = new SpaceService();
			value = spaceService.queryParents(client, this.param.getSpaceid());
			break;
		case JOIN_SPACES:
			spaceService = new SpaceService();
			value = spaceService.queryJoins(client, this.param.getSpaceid(), pageindex, pagesize);
			break;
		case VISIBLE_SPACES:
			spaceService = new SpaceService();
			value = spaceService.queryVisibles(client, this.param.getSpaceid(), pageindex, pagesize);
			break;
		case STAR_SPACES:
			if (this.param.getSpace() != null) {
				if (SpaceHandler.isUsers(this.param.getSpace())) {
					IUserService userService = new UserService();
					UserBean user = userService.getBySpaceid(this.param.getSpaceid());
					if (user != null) {
						spaceService = new SpaceService();
						value = spaceService.queryStars(client, user.getId(), pageindex, pagesize);
					}
				}
			}
			break;
		case SPACE_EVENTS:
			ISpaceEventService spaceEventService = new SpaceEventService();
			param.put("spaceid", this.param.getSpaceid());
			PageSqlParam pageSqlParam = new PageSqlParam(null, null, param);
			pageSqlParam.setPageindex(pageindex);
			pageSqlParam.setPagesize(pagesize);
			value = spaceEventService.queryPage(pageSqlParam);
			break;
		case SPACE_TEAMS:
			ISpaceTeamService spaceTeamService = new SpaceTeamService();
			param.put("spaceid", this.param.getSpaceid());
			pageSqlParam = new PageSqlParam(null, null, param);
			pageSqlParam.setPageindex(pageindex);
			pageSqlParam.setPagesize(pagesize);
			value = spaceTeamService.queryPage(pageSqlParam);
			break;

		}
		outData(messageID, modelType.getValue(), value);
	}

	public void outByThisSpace(JSONObject message) {
		Set<EngineSession> sessions = EngineCache.getSessions();
		if (StringUtil.isEmpty(this.session.spaceid)) {
			return;
		}
		for (EngineSession session : sessions) {
			if (StringUtil.isNotEmpty(session.spaceid) && session.spaceid.equals(this.session.spaceid)) {
				session.sendMessage(message);
			}
		}
	}

	public void outByThisSpaceBranch(JSONObject message) {
		Set<EngineSession> sessions = EngineCache.getSessions();
		if (StringUtil.isEmpty(this.session.spaceid) || StringUtil.isEmpty(this.session.branch)) {
			return;
		}
		for (EngineSession session : sessions) {
			if (StringUtil.isNotEmpty(session.spaceid) && session.spaceid.equals(this.session.spaceid)
					&& StringUtil.isNotEmpty(session.branch) && session.spaceid.equals(this.session.branch)) {
				session.sendMessage(message);
			}
		}
	}
}
