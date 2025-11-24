const API_BASE = '/api';

function saveToken(tok) { localStorage.setItem('jwt', tok); }
function getToken() { return localStorage.getItem('jwt'); }
function clearToken() { localStorage.removeItem('jwt'); }
function authHeaders() {
  const t = getToken();
  return t ? { 'Authorization': 'Bearer ' + t } : {};
}

function showAlert(msg, type='danger') {
  const a = document.getElementById('alert');
  if (!a) return;
  a.className = 'alert alert-' + type;
  a.textContent = msg;
  a.classList.remove('d-none');
  setTimeout(()=> a.classList.add('d-none'), 3500);
}

document.addEventListener('submit', async (e) => {
  if (e.target && e.target.id === 'loginForm') {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
      const res = await fetch(API_BASE + '/auth/login', {
        method: 'POST',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify({ username, password })
      });
      if (!res.ok) {
        showAlert('Invalid credentials');
        return;
      }
      const data = await res.json();
      const token = data.token || data.accessToken || data.jwt;
      saveToken(token);
      window.location = '/feed';
    } catch (err) {
      showAlert('Network error');
    }
  }

  if (e.target && e.target.id === 'registerForm') {
    e.preventDefault();
    const body = {
      name: document.getElementById('name').value,
      email: document.getElementById('email').value,
      username: document.getElementById('username').value,
      password: document.getElementById('password').value
    };
    try {
      const res = await fetch(API_BASE + '/auth/register', {
        method: 'POST',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify(body)
      });
      if (!res.ok) {
        showAlert('Could not register');
        return;
      }
      showAlert('Registered! You can login now', 'success');
      setTimeout(()=> window.location='/login',1200);
    } catch (err) {
      showAlert('Network error');
    }
  }

  if (e.target && e.target.id === 'createPostForm') {
    e.preventDefault();
    const text = document.getElementById('postText').value;
    const filesEl = document.getElementById('postFiles');
    const fd = new FormData();
    if (text) fd.append('text', text);
    if (filesEl && filesEl.files) {
      for (let f of filesEl.files) fd.append('files', f);
    }

    try {
      const res = await fetch(API_BASE + '/posts', {
        method: 'POST',
        headers: {
          ...authHeaders()
        },
        body: fd
      });
      if (!res.ok) {
        const err = await res.json().catch(()=>null);
        showAlert(err?.message || 'Failed to post');
        return;
      }
      const post = await res.json();
      document.getElementById('postText').value = '';
      filesEl.value = null;
      loadFeed();
    } catch (err) {
      showAlert('Network error');
    }
  }
});

const btnLogout = document.getElementById('btnLogout');
if (btnLogout) {
  btnLogout.addEventListener('click', () => {
    clearToken();
    window.location = '/login';
  });
}

async function loadFeed() {
  const c = document.getElementById('feedContainer');
  if (!c) return;
  c.innerHTML = '<p>Loading...</p>';
  try {
    const res = await fetch(API_BASE + '/posts', { headers: authHeaders() });
    if (!res.ok) {
      if (res.status === 401 || res.status === 403) {
        window.location = '/login';
        return;
      }
      c.innerHTML = '<p>Failed to load</p>';
      return;
    }
    const posts = await res.json();
    c.innerHTML = posts.map(renderPostCard).join('');
  } catch(err) {
    c.innerHTML = '<p>Network error</p>';
  }
}

function renderPostCard(p) {
  const medias = (p.mediaList || []).map(m => {
    if (m.mediaType === 'IMAGE' || m.mediaType === 'IMAGE') {
      return `<img class="post-img" src="${m.url}" alt="">`;
    } else if (m.mediaType === 'VIDEO') {
      return `<video controls style="max-width:100%"><source src="${m.url}"></video>`;
    } else {
      return '';
    }
  }).join('');

  return `
  <div class="card mb-3">
    <div class="card-body">
      <div class="d-flex justify-content-between">
        <div><strong>${p.author.username}</strong></div>
        <div><small>${new Date(p.createdAt || p.timestamp || Date.now()).toLocaleString()}</small></div>
      </div>
      <p class="mt-2">${p.text || ''}</p>
      <div>${medias}</div>
      <div class="post-actions mt-2">
        <button class="btn btn-sm btn-outline-primary" onclick="likePost(${p.id})">Like</button>
        <a class="btn btn-sm btn-link" href="/post/${p.id}">Open</a>
      </div>
    </div>
  </div>`;
}

async function loadSinglePost(id) {
  const container = document.getElementById('postContainer');
  if (!container) return;
  container.innerHTML = '<p>Loading...</p>';
  try {
    const res = await fetch(API_BASE + '/posts/' + id, { headers: authHeaders() });
    if (!res.ok) {
      container.innerHTML = '<p>Not found or not allowed</p>';
      return;
    }
    const p = await res.json();
    container.innerHTML = renderPostCard(p);
  } catch(err) {
    container.innerHTML = '<p>Network error</p>';
  }
}

async function likePost(id) {
  try {
    const res = await fetch(API_BASE + '/posts/' + id + '/like', {
      method: 'POST',
      headers: { ...authHeaders() }
    });
    if (!res.ok) {
      const t = await res.text().catch(()=>null);
      showAlert(t || 'Failed', 'warning');
      return;
    }
    showAlert('Liked', 'success');
    loadFeed();
  } catch(err) {
    showAlert('Network error');
  }
}

document.addEventListener('DOMContentLoaded', () => {
  const path = location.pathname;
  const postMatch = path.match(/^\/post\/(\d+)/) || path.match(/^\/posts\/(\d+)/);
  if (postMatch) {
    loadSinglePost(postMatch[1]);
  } else {
    loadFeed();
  }
});
