
<template>
  <form class="os-form p-fluid">
    <template v-for="(field, idx) of form.fields" :key="idx">
      <div class="p-field p-grid">
        <label class="p-col-12 p-mb-3 p-md-3 p-mb-md-0">{{field.label}}</label>
        <div class="p-col-12 p-md-9">
          <component style="width: 100%;" :is="field.type" :field="field" v-model="ctx.data[field.name]">
          </component>
        </div>
      </div>
    </template>
  </form>
</template>

<script>

import Dropdown from '@/common/components/Dropdown.vue';
import Textarea from '@/common/components/Textarea.vue';
import TextField from '@/common/components/InputText.vue';
import RadioButton from '@/common/components/RadioButton.vue';

export default {
  props: ['form', 'data'],

  components: {
    'stringTextField': TextField,
    'radioButton': RadioButton,
    'dropdown': Dropdown,
    'textArea': Textarea,
  },

  setup(props) {
    let ctx = {
      data: props.data
    };

    return {
      ctx
    }
  },
}

</script>

<style scoped>

.os-form label {
 justify-content: flex-end;
 font-weight: bold;
}

.os-form .p-field label,
.os-form .p-field div {
  padding-left: 1rem;
  padding-right: 1rem;
}

.os-form .p-field {
  margin-bottom: 20px;
}

</style>
