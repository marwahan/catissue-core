
<template>
  <span>
    <Button :label="label" right-icon="caret-down" @click="toggle" />
    <dropdown-menu ref="menu" :model="items" :popup="true">
      <template #item="{item}">
        <a class="p-menuitem-link" @click="item.command">
          <Icon class="os-menu-item-icon" :name="item.icon" v-if="item.icon"></Icon>
          <span>{{item.label}}</span>
        </a>
      </template>
    </dropdown-menu>
  </span>
</template>

<script>
import Menu from 'primevue/menu';
import Button from '@/common/components/Button.vue';
import Icon from '@/common/components/Icon.vue';

export default {
  props: ['label', 'options'],

  components: {
    Button,
    Icon,
    'dropdown-menu': Menu
  },

  methods: {
    toggle(event) {
      this.$refs.menu.toggle(event);
    }
  },

  computed: {
    items: function() {
      return this.options.map(({icon, caption, onSelect}) => { return {icon: icon, label: caption, command: onSelect} } );
    }
  }
}
</script>

<style scoped>
  .os-menu-item-icon {
    padding-right: 15px;
  }
</style>
