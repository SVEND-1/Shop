// // src/main/resources/static/js/auth.js
// document.addEventListener('DOMContentLoaded', function() {
//     // Инициализация форм
//     initForms();
// });
//
// function initForms() {
//     const loginForm = document.getElementById('loginForm');
//     const registerForm = document.getElementById('registerForm');
//     const forgotForm = document.getElementById('forgotForm');
//     const resetForm = document.getElementById('resetForm');
//
//     if (loginForm) {
//         loginForm.addEventListener('submit', handleLogin);
//     }
//
//     if (registerForm) {
//         registerForm.addEventListener('submit', handleRegister);
//     }
//
//     if (forgotForm) {
//         forgotForm.addEventListener('submit', handleForgotPassword);
//     }
//
//     if (resetForm) {
//         resetForm.addEventListener('submit', handleResetPassword);
//     }
// }
//
// async function handleLogin(e) {
//     e.preventDefault();
//     const formData = new FormData(e.target);
//     const data = Object.fromEntries(formData);
//
//     try {
//         const response = await fetch('/api/auth/login', {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json'
//             },
//             body: JSON.stringify(data)
//         });
//
//         const result = await response.json();
//
//         if (response.ok) {
//             alert('Вход выполнен успешно!');
//             window.location.href = '/';
//         } else {
//             alert(result.message || 'Ошибка входа');
//         }
//     } catch (error) {
//         alert('Ошибка сети');
//     }
// }
//
// async function handleRegister(e) {
//     e.preventDefault();
//     const formData = new FormData(e.target);
//     const data = Object.fromEntries(formData);
//
//     if (data.password !== data.confirmPassword) {
//         alert('Пароли не совпадают');
//         return;
//     }
//
//     try {
//         const response = await fetch('/api/auth/register', {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json'
//             },
//             body: JSON.stringify(data)
//         });
//
//         const result = await response.json();
//
//         if (response.ok) {
//             alert('Регистрация успешна!');
//             window.location.href = '/login';
//         } else {
//             alert(result.message || 'Ошибка регистрации');
//         }
//     } catch (error) {
//         alert('Ошибка сети');
//     }
// }
//
// async function handleForgotPassword(e) {
//     e.preventDefault();
//     const formData = new FormData(e.target);
//     const data = Object.fromEntries(formData);
//
//     try {
//         const response = await fetch('/api/auth/forgot-password', {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json'
//             },
//             body: JSON.stringify(data)
//         });
//
//         const result = await response.json();
//
//         if (response.ok) {
//             alert('Ссылка для восстановления отправлена на email');
//         } else {
//             alert(result.message || 'Ошибка отправки');
//         }
//     } catch (error) {
//         alert('Ошибка сети');
//     }
// }
//
// async function handleResetPassword(e) {
//     e.preventDefault();
//     const formData = new FormData(e.target);
//     const data = Object.fromEntries(formData);
//
//     if (data.newPassword !== data.confirmPassword) {
//         alert('Пароли не совпадают');
//         return;
//     }
//
//     try {
//         const response = await fetch('/api/auth/reset-password', {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json'
//             },
//             body: JSON.stringify(data)
//         });
//
//         const result = await response.json();
//
//         if (response.ok) {
//             alert('Пароль успешно изменен');
//             window.location.href = '/login';
//         } else {
//             alert(result.message || 'Ошибка смены пароля');
//         }
//     } catch (error) {
//         alert('Ошибка сети');
//     }
// }