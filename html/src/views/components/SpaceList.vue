<template>
  <ul class="coos-list pdtb-20">
    <template v-if="list != null">
      <li v-if="list.length == 0">
        <div class="text-center pd-50 font-lg color-orange">暂无仓库数据</div>
      </li>
      <li v-for="( one , index ) in list" :key="index" class style="border-bottom: 1px solid #ddd;">
        <div class="coos-item">
          <div class="coos-index">{{one.name.substring( 0 , 1 ).toUpperCase() }}</div>
          <div class="coos-content">
            <div class>
              <a :href="app.getSpaceUrl(one)" class="coos-link color-green ft-16">{{one.name}}</a>
              <a class="color-grey mgl-10">
                <i
                  v-if="one.type == 'ORGANIZATIONS'"
                  title="组织"
                  class="coos-icon coos-icon-cluster ft-14"
                ></i>
                <i
                  v-if="one.type == 'ENTERPRISES'"
                  title="企业"
                  class="coos-icon coos-icon-deploymentunit ft-14"
                ></i>
                <i
                  v-if="one.type == 'PRODUCTS'"
                  title="产品"
                  class="coos-icon coos-icon-appstore ft-14"
                ></i>
                <i
                  v-if="one.type == 'REPOSITORYS'"
                  title="仓库"
                  class="coos-icon coos-icon-code ft-14"
                ></i>
              </a>
            </div>
            <div class="ft-14">{{coos.isEmpty(one.description)?'暂无描述':one.description}}</div>
          </div>
          <div class="coos-flex-right">
            <a v-if="one.permission == 'MASTER'" class="color-orange mgl-5" title="管理员权限">
              <i class="coos-icon coos-icon-manager"></i>
            </a>
            <a v-if="one.permission == 'DEVELOPER'" class="color-orange mgl-5" title="开发权限">
              <i class="coos-icon coos-icon-code"></i>
            </a>
            <a v-if="one.permission == 'VIEWER'" class="color-orange mgl-5" title="只读权限">
              <i class="coos-icon coos-icon-eye"></i>
            </a>

            <a v-if="one.publictype == 'PRIVATE'" class="color-orange mgl-5" title="私人">
              <i class="coos-icon coos-icon-lock"></i>
            </a>
            <a v-if="one.publictype == 'OPEN'" class="color-orange mgl-5" title="开放">
              <i class="coos-icon coos-icon-unlock"></i>
            </a>
            <a
              v-if="!one.login_user_star"
              @click="star(one)"
              class="mgl-5 color-orange coos-pointer"
              title="星标"
            >
              <i class="coos-icon coos-icon-star"></i>
            </a>
            <a v-else class="mgl-5 color-orange coos-pointer" @click="unstar(one)" title="去除星标">
              <i class="coos-icon coos-icon-star-fill"></i>
            </a>
          </div>
        </div>
      </li>
    </template>
  </ul>
</template>

<script>
export default {
  name: "Header",
  data() {
    return {
      source: source
    };
  },
  props: ["list"],
  beforeMount() {},
  watch: {},
  methods: {
    star(space) {
      source
        .do("SPACE_STAR_INSERT", {
          spaceid: space.id,
          userid: source.LOGIN_USER.id
        })
        .then(res => {
          space.login_user_star = true;
        });
    },
    unstar(space) {
      source
        .do("SPACE_STAR_DELETE", {
          spaceid: space.id,
          userid: source.LOGIN_USER.id
        })
        .then(res => {
          space.login_user_star = false;
        });
    }
  },
  computed: {},
  mounted() {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
