
<template>
  <form novalidate>
    <div class="row" v-for="(formRow, rowIdx) of ctx.formRows" :key="rowIdx">
      <div class="field" v-for="(field, fieldIdx) of formRow" :key="rowIdx + '_' + fieldIdx">
        <label>{{field.label}}</label>
        <component :is="field.component" v-bind="field" v-model="ctx.formData[field.name]"
          @update:model-value="$emit('input', {field: field, data: ctx.formData})">
        </component>
      </div>
    </div>

    <div class="row">
      <div class="field">
        <slot></slot>
      </div>
    </div>
  </form>
</template>

<script>

import { reactive } from 'vue';

import fieldFactory from '@/common/services/FieldFactory.js';
import Dropdown from '@/common/components/Dropdown.vue';
import InputText from '@/common/components/InputText.vue';
import RadioButton from '@/common/components/RadioButton.vue';
import Textarea from '@/common/components/Textarea.vue';

export default {
   props: ['schema', 'data'],

   components: {
     Dropdown,
     InputText,
     RadioButton,
     Textarea
   },

   emits: ['input'],

   setup(props) {
     let ctx = reactive({
       formRows: [],

       formData: props.data
     });

     for (let row of props.schema.rows) {
       let formRow = [];
       ctx.formRows.push(formRow);

       for (let field of row.fields) {
         let component = fieldFactory.getComponent(field.type);
         formRow.push({...field, component: component});
       }
     } 

     return {
       ctx
     };
   },

   watch: {
     'ctx.formData': {
       deep: true,

       handler: function(newVal) { console.log('Handler: '); console.log (newVal); console.log ('----'); }
     }
   }
}

</script>

<style scoped>

.row {
  display: flex;
}

.row .field label {
  font-weight: bold;
}

.row .field {
  flex: 1 1 0;
  padding: 0.5rem 1rem;
}

.row .field :deep(.btn) {
  margin-right: 0.5rem;
}
</style>
