/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://www.mozilla.org/MPL/2.0/.
 */

const CACHE_NAME = 'voiz-cache-v1';
const URLS_TO_CACHE = [
  '/',
  '/offline',
  '/css/styles.css',
  '/js/multi-step-form.js',
  '/js/report-form.js',
  '/js/schedule-form.js',
  '/about',
  '/schedule',
  '/track',
  '/manifest.json',
  '/favicon.svg',
  '/js/pwa.js'
];

self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => {
        console.log('Opened cache');
        return cache.addAll(URLS_TO_CACHE);
      })
  );
});

self.addEventListener('fetch', event => {
  event.respondWith(
    caches.match(event.request)
      .then(response => {
        // If the request is for a navigation (HTML document)
        if (event.request.destination === 'document') {
          return fetch(event.request)
            .then(networkResponse => {
              // If the network request succeeds, return it
              if (networkResponse.status === 200) {
                return networkResponse;
              }
              // If it fails, return the offline page from cache
              return caches.match('/offline');
            })
            .catch(() => {
              // Network request failed (offline), return offline page
              return caches.match('/offline');
            });
        }
        
        // For non-document requests, return from cache or network
        if (response) {
          return response;
        }
        
        return fetch(event.request)
          .then(networkResponse => {
            return networkResponse;
          })
          .catch(() => {
            // For non-HTML requests when offline, return error response
            return new Response('Not available offline', {
              status: 503,
              headers: { 'Content-Type': 'text/plain' }
            });
          });
      })
  );
});

self.addEventListener('activate', event => {
  const cacheWhitelist = [CACHE_NAME];
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(cacheName => {
          if (cacheWhitelist.indexOf(cacheName) === -1) {
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
});
