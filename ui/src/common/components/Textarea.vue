
<template>
  <div class="os-input-text">
    <div class="p-float-label" v-if="field.mdType">
      <p-textarea v-model="inputValue" rows="2"/>
      <label>{{field.label}}</label>
    </div>
    <div v-else>
      <p-textarea v-model="inputValue" :placeholder="field.placeholder" :rows="field.rows" :cols="field.cols"/>
    </div>
  </div>
</template>

<script>
import Textarea from 'primevue/textarea';

export default {
  props: ['modelValue', 'field'],

  components: {
    'p-textarea': Textarea
  },

  data() {
    return {
    }
  },

  computed: {
    inputValue: {
      get() {
        return this.modelValue;
      },

      set(value) {
        this.$emit('update:modelValue', value);
      }
    }
  },

  methods: {
  }
}
</script>

<style scoped>
  .os-input-text .p-float-label {
    margin-top: 10px;
  }

  .os-input-text .p-float-label :deep(.p-inputtext) {
    border: 0px;
    border-bottom: 2px solid #ced4da;
    border-radius: 0px;
    padding: 2px 0px;
    box-shadow: none;
  }

  .os-input-text .p-float-label :deep(.p-inputtext:enabled:focus) {
    border-bottom-color: #007bff;
  }

  .os-input-text .p-float-label :deep(label) {
    left: 0rem;
  }

  .os-input-text .p-float-label :deep(.p-inputtext:not(:enabled:focus) ~ label) {
    color: #999;
    opacity: 1;
  }

  .os-input-text :deep(textarea) {
    resize: none;
  }
</style>
