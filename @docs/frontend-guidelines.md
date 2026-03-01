# Vue 3 Frontend Guidelines

This document provides guidelines for Vue 3 application development with TypeScript.

## 1 Project Setup

### 1.1 Tech Stack
- Build Tool: Vite
- Framework: Vue 3
- Language: TypeScript (strict mode)
- State Management: Pinia
- Router: Vue Router 4
- HTTP Client: Axios

### 1.2 Recommended Project Structure
```
src/
├── assets/           # Static assets
├── components/       # Reusable components
│   ├── common/       # Generic components (Button, Input, Modal)
│   └── layout/       # Layout components (Header, Sidebar)
├── composables/      # Composable functions
├── layouts/          # Page layouts
├── router/           # Router configuration
├── stores/           # Pinia stores
├── types/            # TypeScript type definitions
├── utils/            # Utility functions
└── views/            # Page components
```

### 1.3 Feature-Based Structure (For Large Projects)
```
src/
├── features/
│   ├── users/
│   │   ├── components/
│   │   ├── composables/
│   │   ├── types/
│   │   └── UserView.vue
│   └── orders/
│       └── ...
└── shared/
    ├── components/
    └── utils/
```

## 2 TypeScript Configuration

### 2.1 tsconfig.json
Enable strict mode for maximum type safety.

```json
{
  "compilerOptions": {
    "target": "ESNext",
    "module": "ESNext",
    "moduleResolution": "bundler",
    "strict": true,
    "jsx": "preserve",
    "resolveJsonModule": true,
    "isolatedModules": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "noEmit": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  }
}
```

### 2.2 Type Definitions
Create dedicated type files for shared types.

```typescript
// src/types/user.ts
export interface User {
  id: string;
  username: string;
  email: string;
  role: 'admin' | 'user' | 'guest';
  createdAt: string;
}

export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
}
```

## 3 Component Development

### 3.1 Composition API
Use `<script setup>` with Composition API.

```vue
<script setup lang="ts">
import { ref, computed } from 'vue';

interface Props {
  title: string;
  items: Item[];
  loading?: boolean;
}

interface Emits {
  (e: 'select', item: Item): void;
  (e: 'delete', id: string): void;
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
});

const emit = defineEmits<Emits>();

const selectedId = ref<string | null>(null);

const selectedItem = computed(() => 
  props.items.find(item => item.id === selectedId.value)
);

function handleSelect(item: Item) {
  selectedId.value = item.id;
  emit('select', item);
}
</script>
```

### 3.2 Props Definition
Always define props with proper types and validation.

```vue
<script setup lang="ts">
// Using TypeScript-style syntax (recommended)
interface Props {
  title: string;
  count?: number;
  items: string[];
}

const props = withDefaults(defineProps<Props>(), {
  count: 0,
});

// Or with runtime validation
const props = defineProps({
  title: {
    type: String,
    required: true,
  },
  count: {
    type: Number,
    default: 0,
  },
});
</script>
```

### 3.3 Emits Definition
Type emits properly for type safety.

```vue
<script setup lang="ts">
const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
  (e: 'submit', payload: SubmitPayload): void;
  (e: 'cancel'): void;
}>();

// Usage
emit('update:modelValue', 'new value');
emit('submit', { username: 'test', email: 'test@test.com' });
</script>
```

### 3.4 Component Naming
- Use PascalCase for component file names
- Use multi-word names (avoid single generic names)
- Prefix base components with their type

```
UserCard.vue
BaseButton.vue
AppHeader.vue
```

### 3.5 Component Structure
Organize component sections in this order:

1. `<script setup>` with imports
2. Type definitions (if needed inline)
3. Props and emits
4. Reactive state
5. Computed properties
6. Watchers
7. Lifecycle hooks
8. Methods
9. Template
10. Style (scoped)

## 4 State Management (Pinia)

### 4.1 Store Structure
Use Pinia for global state management.

```typescript
// src/stores/userStore.ts
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { User } from '@/types/user';
import { userApi } from '@/api/user';

export const useUserStore = defineStore('user', () => {
  // State
  const users = ref<User[]>([]);
  const currentUser = ref<User | null>(null);
  const loading = ref(false);

  // Getters
  const activeUsers = computed(() => 
    users.value.filter(user => user.status === 'active')
  );

  const userCount = computed(() => users.value.length);

  // Actions
  async function fetchUsers() {
    loading.value = true;
    try {
      users.value = await userApi.getUsers();
    } finally {
      loading.value = false;
    }
  }

  async function fetchUserById(id: string) {
    currentUser.value = await userApi.getUser(id);
  }

  return {
    users,
    currentUser,
    loading,
    activeUsers,
    userCount,
    fetchUsers,
    fetchUserById,
  };
});
```

### 4.2 Store Naming
- Use descriptive names: `useUserStore`, `useCartStore`
- One store per domain/feature
- Avoid giant stores; split when needed

## 5 Composables

### 5.1 Creating Composables
Extract reusable logic into composables.

```typescript
// src/composables/usePagination.ts
import { ref, computed } from 'vue';

export function usePagination<T>(items: Ref<T[]>, initialPageSize = 10) {
  const currentPage = ref(1);
  const pageSize = ref(initialPageSize);

  const totalPages = computed(() => 
    Math.ceil(items.value.length / pageSize.value)
  );

  const paginatedItems = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value;
    const end = start + pageSize.value;
    return items.value.slice(start, end);
  });

  function nextPage() {
    if (currentPage.value < totalPages.value) {
      currentPage.value++;
    }
  }

  function prevPage() {
    if (currentPage.value > 1) {
      currentPage.value--;
    }
  }

  return {
    currentPage,
    pageSize,
    totalPages,
    paginatedItems,
    nextPage,
    prevPage,
  };
}
```

### 5.2 Composable Naming
- Prefix with `use`: `useUser`, `usePagination`, `useFormValidation`
- Return typed objects
- Document parameters and return values

## 6 API Calls

### 6.1 API Service Layer
Centralize API calls in service files.

```typescript
// src/api/user.ts
import axios from 'axios';
import type { User, CreateUserRequest, UpdateUserRequest } from '@/types/user';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

export const userApi = {
  async getUsers(): Promise<User[]> {
    const { data } = await api.get<User[]>('/users');
    return data;
  },

  async getUser(id: string): Promise<User> {
    const { data } = await api.get<User>(`/users/${id}`);
    return data;
  },

  async createUser(request: CreateUserRequest): Promise<User> {
    const { data } = await api.post<User>('/users', request);
    return data;
  },

  async updateUser(id: string, request: UpdateUserRequest): Promise<User> {
    const { data } = await api.put<User>(`/users/${id}`, request);
    return data;
  },

  async deleteUser(id: string): Promise<void> {
    await api.delete(`/users/${id}`);
  },
};
```

### 6.2 Error Handling
Handle API errors consistently.

```typescript
try {
  await userApi.getUser(id);
} catch (error) {
  if (axios.isAxiosError(error)) {
    if (error.response?.status === 404) {
      // Handle 404
    } else {
      // Handle other errors
    }
  }
  throw error;
}
```

## 7 Routing

### 7.1 Route Configuration
Use typed router configuration.

```typescript
// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomeView.vue'),
  },
  {
    path: '/users',
    name: 'Users',
    component: () => import('@/views/UsersView.vue'),
    children: [
      {
        path: ':id',
        name: 'UserDetail',
        component: () => import('@/views/UserDetailView.vue'),
        props: true,
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
```

### 7.2 Programmatic Navigation
Use typed navigation.

```typescript
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

// Navigate with typed params
router.push({ name: 'UserDetail', params: { id: '123' } });

// Get typed params
const userId = route.params.id as string;
```

## 8 Styling

### 8.1 Scoped Styles
Always use scoped styles to prevent style leakage.

```vue
<style scoped>
.container {
  padding: 16px;
}

.title {
  font-size: 20px;
}
</style>
```

### 8.2 CSS Variables
Use CSS variables for theming.

```css
:root {
  --primary-color: #1890ff;
  --text-color: #333;
  --border-radius: 4px;
}

.button {
  background-color: var(--primary-color);
  border-radius: var(--border-radius);
}
```

### 8.3 Preprocessors
Use SCSS for better CSS organization.

```vue
<style scoped lang="scss">
$primary-color: #1890ff;

.button {
  background-color: $primary-color;
  
  &:hover {
    opacity: 0.8;
  }
  
  &--large {
    padding: 12px 24px;
  }
}
</style>
```

## 9 Best Practices

### 9.1 Reactivity
- Use `ref` for primitive values
- Use `reactive` for objects
- Avoid mixing ref and reactive

```typescript
// Good
const count = ref(0);
const user = reactive({ name: 'John', age: 30 });

// Avoid
const user = ref({ name: 'John', age: 30 });
```

### 9.2 Computed Properties
Use computed properties for derived state.

```typescript
const items = ref<number[]>([1, 2, 3, 4, 5]);
const doubled = computed(() => items.value.map(n => n * 2));
const hasItems = computed(() => items.value.length > 0);
```

### 9.3 Watchers
Use watchers for side effects.

```typescript
const searchQuery = ref('');

watch(searchQuery, async (newQuery) => {
  if (newQuery) {
    results.value = await searchApi(newQuery);
  }
}, { immediate: true });
```

### 9.4 Avoid Any
Never use `any` unless absolutely necessary.

```typescript
// Bad
const data: any = fetchData();

// Good
const data: unknown = fetchData();
if (isUserData(data)) {
  // use data
}
```

### 9.5 Template Refs
Type template refs properly.

```vue
<script setup lang="ts">
import { ref } from 'vue';
import type { ElTable } from 'element-plus';

const tableRef = ref<InstanceType<typeof ElTable>>();

function handleScroll() {
  tableRef.value?.scrollTo(0);
}
</script>

<template>
  <el-table ref="tableRef">...</el-table>
</template>
```

## 10 Testing

### 10.1 Unit Tests
Use Vitest for unit testing.

```typescript
// src/composables/__tests__/usePagination.spec.ts
import { describe, it, expect } from 'vitest';
import { ref } from 'vue';
import { usePagination } from '../usePagination';

describe('usePagination', () => {
  it('should paginate items', () => {
    const items = ref([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]);
    const { paginatedItems, totalPages } = usePagination(items, 3);
    
    expect(totalPages.value).toBe(4);
    expect(paginatedItems.value).toEqual([1, 2, 3]);
  });
});
```

### 10.2 Component Tests
Use Vue Test Utils for component testing.

```typescript
import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import BaseButton from '../BaseButton.vue';

describe('BaseButton', () => {
  it('emits click event', async () => {
    const wrapper = mount(BaseButton, {
      props: { label: 'Click me' },
    });
    
    await wrapper.trigger('click');
    
    expect(wrapper.emitted('click')).toBeTruthy();
  });
});
```

### 10.3 E2E Tests
Use Playwright for end-to-end testing.

```typescript
import { test, expect } from '@playwright/test';

test('user can login', async ({ page }) => {
  await page.goto('/login');
  await page.fill('[data-testid="email"]', 'test@example.com');
  await page.fill('[data-testid="password"]', 'password');
  await page.click('[data-testid="submit"]');
  
  await expect(page).toHaveURL('/dashboard');
});
```

## 11 Code Quality

### 11.1 ESLint Configuration
Use strict ESLint rules.

```javascript
// .eslintrc.cjs
module.exports = {
  extends: [
    'plugin:vue/vue3-recommended',
    'plugin:@typescript-eslint/recommended',
    'prettier',
  ],
  rules: {
    'vue/multi-word-component-names': 'off',
    '@typescript-eslint/no-unused-vars': 'error',
  },
};
```

### 11.2 Prettier Configuration
Use Prettier for consistent formatting.

```json
{
  "semi": true,
  "singleQuote": true,
  "tabWidth": 2,
  "trailingComma": "es5",
  "printWidth": 100
}
```

### 11.3 Editor Config
Include .editorconfig for consistent editor settings.

```ini
root = true

[*]
charset = utf-8
indent_style = space
indent_size = 2
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true
```

## 12 Performance Optimization

### 12.1 Component Lazy Loading
Lazy load routes and heavy components.

```typescript
// Route lazy loading
{
  path: '/admin',
  component: () => import('@/views/AdminView.vue'),
}

// Component lazy loading
const HeavyChart = defineAsyncComponent(() => 
  import('@/components/HeavyChart.vue')
);
```

### 12.2 v-memo
Use v-memo for optimized list rendering.

```vue
<template>
  <div v-for="item in items" :key="item.id" v-memo="[item.selected]">
    <ComplexComponent :item="item" />
  </div>
</template>
```

### 12.3 markRaw
Use markRaw for objects that don't need reactivity.

```typescript
import { markRaw } from 'vue';

const chartInstance = markRaw(new Chart({
  // ...
}));
```
