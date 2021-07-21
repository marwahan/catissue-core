<template>
  <Page>
    <PageHeader>
      <template #breadcrumb>
        <Breadcrumb :items="ctx.bcrumb" />
      </template>

      <span>
        <h3 v-if="!ctx.user.id">Add User</h3>
        <h3 v-if="ctx.user.id">Edit {{ctx.user.firstName}} {{ctx.user.lastName}}</h3>
      </span>
    </PageHeader>
    <PageBody>
      <div style="width: 75%;">
        <Form :form="form" :data="ctx.user" />
      </div>
    </PageBody>
  </Page>
</template>

<script>
import { reactive, inject } from 'vue';

import Page from '@/common/components/Page.vue';
import PageHeader from '@/common/components/PageHeader.vue';
import Breadcrumb from '@/common/components/Breadcrumb.vue';
import PageBody from '@/common/components/PageBody.vue';

import Form from '@/common/components/Form.vue';

import authDomainSvc from '@/administrative/services/AuthDomain.js';
import instituteSvc from '@/administrative/services/Institute.js';
import siteSvc from '@/administrative/services/Site.js';
import timeZoneSvc from '@/common/services/TimeZone.js';
import userSvc from '@/administrative/services/User.js';

export default {
  name: 'UserAddEdit',

  inject: ['ui'],

  props: ['userId'],

  components: {
    Page,
    PageHeader,
    Breadcrumb,
    PageBody,
    Form,
  },

  setup(props) {
    const ui = inject('ui');

    let ctx = reactive({
      bcrumb: [ {url: ui.ngServer + '#/users', label: 'Users', target: '_parent'} ],
      user: {dnd: false}
    });

    let domains = null;
    let defInstitutes = null;

    let form = {
      fields: [
        { name: 'type', type: 'radioButton', label: 'Type',
          options: [
            { label: 'Super Administrator', value: 'SUPER' },
            { label: 'Institute Administrator', value: 'INSTITUTE' },
            { label: 'Contact', value: 'CONTACT' },
            { label: 'Regular', value: 'NONE' }
          ]
        },
        { name: 'firstName', type: 'stringTextField', label: 'First Name' },
        { name: 'lastName', type: 'stringTextField', label: 'Last Name' },
        { name: 'domainName', type: 'dropdown', label: 'Domain Name',
          listSource: {
            displayProp: 'name',
            selectProp: 'name',
            loadFn: () => new Promise((resolve) => {
              if (domains) {
                resolve(domains);
                return;
              }

              authDomainSvc.getDomains().then((result) => {
                domains = result;
                resolve(domains);
              });
            })
          }
        },
        { name: 'loginName', type: 'stringTextField', label: 'Login Name' },
        { name: 'instituteName', type: 'dropdown', label: 'Institute',
          listSource: {
            displayProp: 'name',
            selectProp: 'name',
            loadFn: (opts) => new Promise((resolve) => {
              opts = opts || {};
              if (defInstitutes && (!opts.query || defInstitutes.length < 100)) {
                resolve(defInstitutes);
                return;
              }
              
              instituteSvc.getInstitutes({name: opts.query || ''}).then((result) => {
                if (!opts.query) {
                  defInstitutes = result;
                }
                resolve(result);
              });
            })
          }
        },
        { name: 'primarySite', type: 'dropdown', label: 'Primary Site',
          listSource: {
            displayProp: 'name',
            selectProp: 'name',
            loadFn: (opts) => new Promise((resolve) => {
              if (!ctx.user.instituteName) {
                resolve([]);
              }

              opts = opts || {};
              let req = {institute: ctx.user.instituteName, name: opts.query || ''};
              siteSvc.getSites(req).then((result) => resolve(result));
            })
          }
        },
        { name: 'timeZone', type: 'dropdown', label: 'Time Zone',
          listSource: {
            displayProp: 'name',
            selectProp: 'id',
            loadFn: () => timeZoneSvc.getTimeZones()
          }
        },
        { name: 'manageForms', type: 'radioButton', label: 'Manage Forms?',
          options: [
            { label: 'Yes', value: true },
            { label: 'No', value: false }
          ]
        },
        { name: 'dnd', type: 'radioButton', label: 'Disable Notifications?',
          options: [
            { label: 'Yes', value: true },
            { label: 'No', value: false }
          ]
        },
        { name: 'apiUser', type: 'radioButton', label: 'API User?',
          options: [
            { label: 'Yes', value: true },
            { label: 'No', value: false }
          ]
        },
        { name: 'ipRange', type: 'stringTextField', label: 'IP Address' },
        { name: 'address', type: 'textArea', label: 'Address' }
      ]
    };

    alert(props.userId);
    if (props.userId && +props.userId > 0) {
      userSvc.getUserById(props.userId).then(user => Object.assign(ctx.user, user));
    }

    return {
      ctx,

      form
    };
  }  
}
</script>
