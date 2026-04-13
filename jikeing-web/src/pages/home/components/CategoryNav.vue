<template>
  <div class="category">
    <div class="nav" v-if="type == ''">
      <div class="nav_name">
        {{ name }}
      </div>
    </div>

    <div class="nav" v-if="type == 'list'">
        <div class="nav_name">
          <div>
          <router-link :to="{path: '/'}" v-if="from=='list_material'">
            <img src="@/assets/images/themes/back.png" class="back" >
          </router-link>
          </div>
          <div>{{ name }}</div>
        </div>
        <div class="list">
          <div class="list_item">
            <div class="item" :class="{active: item.id==params.id}" v-for="(item,key) in category" :key="key" @click="selectEvent(item)">{{item.name}}</div>
          </div>
          <div class="query" v-if="query_time == 1">
            <el-select v-model="value" placeholder="全部时间" size="small" popper-class="custom_select">
              <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
              </el-option>
            </el-select>
          </div>

        </div>
    </div>

    <div class="nav_more" v-if="type == 'more'">
      <div class="nav_name">
        <router-link :to="{path: '/'}" v-if="from=='list_material'">
          <img src="@/assets/images/themes/back.png" class="back" >
        </router-link>
        {{ name }}
      </div>
    </div>

  </div>
</template>

<script>
import { categoriesList } from '@/api/manager/materials'
export default {
  name: "category",
  props: {
    from: {
      type: String,
      default: ''
    },
    name: {
      type: String,
      default: '动画素材',
    },
    type: {
      type: String,
      default: '',
    },
    query_time: {
      type: String,
      default: '',
    }
  },
  data() {
    return {
      category: [{name: '全部', id:'0'}], //
      value: '',
      options: [
        {value: '3', label: '最近3天'},{value: '5', label: '最近5天'},{value: '7', label: '最近7天'}
      ],
      params: {
        id: '1',
        page: 1,
        page_size: 20,
        from: this.from
      }
    }
  },
  created() {
    this.checkFrom()
  },
  methods: {
    checkFrom() {
      switch (this.from) {
        case 'list_material':
          this.categoriesList()
          break;
        case 'material':
          this.categoriesList()
          break;
        case 'tutorial':
          this.category = []
          break;
      }
    },
    categoriesList() {
      categoriesList({params: this.params}).then(res => {
        if (res.code == 200) {
          this.category.push(...res.data.list)
          if (this.category.length > 0) {
            this.params.id = this.category[0].id
          }
          this.$emit('callfn', this.params)
        }
      })
    },
    selectEvent(item) {
      this.params.id = item.id
      this.params.from = 'index'
      this.$emit('callfn', this.params)
    }
  }
}
</script>

<style scoped lang="scss">
::v-deep .el-input__inner {
  box-sizing: border-box;
  border: 1px solid rgba(255, 255, 255, 0.3);
}
::v-deep .el-select,
::v-deep .el-input,
::v-deep .el-input__inner{
  background: #111827;
  color:#fff;
  border-radius:4px;
  text-align: center;
  color: rgba(255, 255, 255, 0.3);
}
</style>