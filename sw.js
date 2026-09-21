const APP_CACHE='gs-scheduler-app-v22';
const SHARE_CACHE='gs-scheduler-share-v1';
const CORE=[
  './',
  './index.html',
  './manifest.webmanifest',
  './offline.html',
  './icons/icon-192.png',
  './icons/icon-512.png',
  './icons/icon-maskable-512.png',
  './icons/apple-touch-icon.png'
];

self.addEventListener('install',event=>{
  event.waitUntil(caches.open(APP_CACHE).then(c=>c.addAll(CORE)).then(()=>self.skipWaiting()));
});

self.addEventListener('activate',event=>{
  event.waitUntil((async()=>{
    const keys=await caches.keys();
    await Promise.all(keys.filter(k=>k.startsWith('gs-scheduler-app-')&&k!==APP_CACHE).map(k=>caches.delete(k)));
    await self.clients.claim();
  })());
});

self.addEventListener('fetch',event=>{
  const req=event.request;
  const url=new URL(req.url);

  if(req.method==='POST' && url.pathname.endsWith('/share-target')){
    event.respondWith((async()=>{
      try{
        const form=await req.formData();
        const file=form.get('shared_file');
        if(file && typeof file.text==='function'){
          const text=await file.text();
          const cache=await caches.open(SHARE_CACHE);
          const key=new URL('./__shared_import__',self.registration.scope).href;
          await cache.put(key,new Response(text,{headers:{'Content-Type':'text/plain;charset=utf-8'}}));
        }
      }catch(e){}
      return Response.redirect(new URL('./?shared-import=1',self.registration.scope).href,303);
    })());
    return;
  }

  if(req.method!=='GET')return;

  if(req.mode==='navigate'){
    event.respondWith((async()=>{
      try{
        const fresh=await fetch(req);
        const cache=await caches.open(APP_CACHE);
        cache.put('./index.html',fresh.clone());
        return fresh;
      }catch(e){
        const cache=await caches.open(APP_CACHE);
        return (await cache.match('./index.html')) || (await cache.match('./offline.html'));
      }
    })());
    return;
  }

  event.respondWith((async()=>{
    const cached=await caches.match(req);
    if(cached)return cached;
    try{
      const fresh=await fetch(req);
      if(fresh.ok && url.origin===self.location.origin){
        const cache=await caches.open(APP_CACHE);
        cache.put(req,fresh.clone());
      }
      return fresh;
    }catch(e){
      return new Response('',{status:504,statusText:'Offline'});
    }
  })());
});
