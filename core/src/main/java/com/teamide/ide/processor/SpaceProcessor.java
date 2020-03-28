package com.teamide.ide.processor;

import com.alibaba.fastjson.JSONObject;
import com.teamide.client.ClientSession;
import com.teamide.ide.bean.SpaceBean;
import com.teamide.ide.bean.SpaceEventBean;
import com.teamide.ide.bean.SpaceStarBean;
import com.teamide.ide.bean.SpaceTeamBean;
import com.teamide.ide.bean.UserBean;
import com.teamide.ide.enums.SpacePermission;
import com.teamide.ide.handler.SpaceHandler;
import com.teamide.ide.param.SpaceProcessorParam;
import com.teamide.ide.processor.enums.SpaceModelType;
import com.teamide.ide.processor.enums.SpaceProcessorType;
import com.teamide.ide.service.ISpaceEventService;
import com.teamide.ide.service.ISpaceService;
import com.teamide.ide.service.ISpaceTeamService;
import com.teamide.ide.service.IUserService;
import com.teamide.ide.service.impl.SpaceEventService;
import com.teamide.ide.service.impl.SpaceService;
import com.teamide.ide.service.impl.SpaceStarService;
import com.teamide.ide.service.impl.SpaceTeamService;
import com.teamide.ide.service.impl.UserService;

public class SpaceProcessor extends Processor {

	protected final SpaceProcessorParam param;

	public SpaceProcessor(SpaceProcessorParam param) {
		super(param);
		this.param = param;
	}

	public Object onDo(String type, JSONObject data) throws Exception {
		SpaceProcessorType processorType = SpaceProcessorType.get(type);
		if (processorType == null) {
			return super.onDo(type, data);
		}
		return onDo(processorType, data);
	}

	protected Object onDo(SpaceProcessorType processorType, JSONObject data) throws Exception {
		if (processorType == null) {
			return null;
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
			space = spaceService.insert(param.getSession(), space);

			value = SpaceHandler.getFormat(space.getId());

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			break;
		case SPACE_RENAME:
			spaceService = new SpaceService();
			String id = data.getString("id");
			String name = data.getString("name");
			spaceService.rename(param.getSession(), id, name);

			value = SpaceHandler.getFormat(id);
			break;
		case SPACE_UPDATE:
			spaceService = new SpaceService();
			id = data.getString("id");
			String publictype = data.getString("publictype");
			String comment = data.getString("comment");
			space = new SpaceBean();
			space.setId(id);
			space.setPublictype(publictype);
			space.setComment(comment);
			spaceService.update(param.getSession(), space);

			value = SpaceHandler.getFormat(id);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			break;
		case SPACE_DELETE:
			id = data.getString("id");
			space = new SpaceBean();
			space.setId(id);

			spaceService = new SpaceService();
			spaceService.delete(param.getSession(), space);

			spaceEventBean.set(data);
			appendEvent(spaceEventBean);

			break;

		case SPACE_TEAM_INSERT:
			SpaceTeamBean spaceTeamBean = data.toJavaObject(SpaceTeamBean.class);
			SpaceTeamService spaceTeamService = new SpaceTeamService();
			value = spaceTeamService.insert(param.getSession(), spaceTeamBean);

			spaceEventBean.set(data);
			if (value != null) {
				spaceEventBean.setSpaceid(((SpaceTeamBean) value).getSpaceid());
			}
			appendEvent(spaceEventBean);

			break;
		case SPACE_TEAM_UPDATE:
			spaceTeamBean = data.toJavaObject(SpaceTeamBean.class);
			spaceTeamService = new SpaceTeamService();
			value = spaceTeamService.update(param.getSession(), spaceTeamBean);

			spaceEventBean.set(data);
			if (value != null) {
				spaceEventBean.setSpaceid(((SpaceTeamBean) value).getSpaceid());
			}
			appendEvent(spaceEventBean);

			break;
		case SPACE_TEAM_DELETE:
			spaceTeamBean = data.toJavaObject(SpaceTeamBean.class);
			spaceTeamService = new SpaceTeamService();
			value = spaceTeamService.delete(param.getSession(), spaceTeamBean);

			spaceEventBean.set(data);
			if (value != null) {
				spaceEventBean.setSpaceid(((SpaceTeamBean) value).getSpaceid());
			}
			appendEvent(spaceEventBean);

			break;

		case SPACE_STAR_INSERT:
			SpaceStarBean spaceStarBean = data.toJavaObject(SpaceStarBean.class);
			SpaceStarService spaceStarService = new SpaceStarService();
			value = spaceStarService.insert(param.getSession(), spaceStarBean);

			spaceEventBean.set(data);
			if (value != null) {
				spaceEventBean.setSpaceid(((SpaceStarBean) value).getSpaceid());
			}
			appendEvent(spaceEventBean);

			break;
		case SPACE_STAR_UPDATE:
			spaceStarBean = data.toJavaObject(SpaceStarBean.class);
			spaceStarService = new SpaceStarService();
			value = spaceStarService.update(param.getSession(), spaceStarBean);

			spaceEventBean.set(data);
			if (value != null) {
				spaceEventBean.setSpaceid(((SpaceStarBean) value).getSpaceid());
			}
			appendEvent(spaceEventBean);

			break;
		case SPACE_STAR_DELETE:
			spaceStarBean = data.toJavaObject(SpaceStarBean.class);
			spaceStarService = new SpaceStarService();
			value = spaceStarService.delete(param.getSession(), spaceStarBean);

			spaceEventBean.set(data);
			if (value != null) {
				spaceEventBean.setSpaceid(((SpaceStarBean) value).getSpaceid());
			}
			appendEvent(spaceEventBean);

			break;

		}
		return value;
	}

	public Object onLoad(String type, JSONObject data) throws Exception {
		SpaceModelType modelType = SpaceModelType.get(type);
		if (modelType == null) {
			return super.onLoad(type, data);
		}
		return onLoad(modelType, data);
	}

	public Object onLoad(SpaceModelType modelType, JSONObject data) throws Exception {
		if (modelType == null) {
			return null;
		}
		Object value = null;
		ClientSession session = this.param.getSession();
		int pageindex = data.getIntValue("pageindex");
		int pagesize = data.getIntValue("pagesize");
		JSONObject param = new JSONObject();
		switch (modelType) {
		case SPACE:
			JSONObject space_format = SpaceHandler.getFormat(this.param.getSpaceid());
			SpacePermission permission = SpaceHandler.getPermission(this.param.getSpaceid(), session);
			space_format.put("permission", permission);
			value = space_format;
			break;
		case PARENTS:
			ISpaceService spaceService = new SpaceService();
			value = spaceService.queryParents(session, this.param.getSpaceid());
			break;
		case JOIN_SPACES:
			spaceService = new SpaceService();
			value = spaceService.queryJoins(session, this.param.getSpaceid(), pageindex, pagesize);
			break;
		case VISIBLE_SPACES:
			spaceService = new SpaceService();
			value = spaceService.queryVisibles(session, this.param.getSpaceid(), pageindex, pagesize);
			break;
		case STAR_SPACES:
			if (this.param.getSpaceid() != null) {
				SpaceBean space = SpaceHandler.get(this.param.getSpaceid());
				if (SpaceHandler.isUsers(space)) {
					IUserService userService = new UserService();
					UserBean user = userService.getBySpaceid(this.param.getSpaceid());
					if (user != null) {
						spaceService = new SpaceService();
						value = spaceService.queryStars(session, user.getId(), pageindex, pagesize);
					}
				}
			}
			break;
		case SPACE_EVENTS:
			ISpaceEventService spaceEventService = new SpaceEventService();
			param.put("spaceid", this.param.getSpaceid());
			value = spaceEventService.queryPage(param, pageindex, pagesize);
			break;
		case SPACE_TEAMS:
			ISpaceTeamService spaceTeamService = new SpaceTeamService();
			param.put("spaceid", this.param.getSpaceid());
			value = spaceTeamService.queryPage(param, pageindex, pagesize);

			break;
		}
		return value;
	}

}
