
class FieldFactory {

  fieldTypes = {
    dropdown: 'Dropdown',
    radio: 'RadioButton',
    text: 'InputText',
    textarea: 'Textarea'
  };

  getComponent(fieldType) {
    let component = this.fieldTypes[fieldType];
    if (!component) {
      component = 'Unknown Component';
    }

    return component;
  }

}

export default new FieldFactory();
