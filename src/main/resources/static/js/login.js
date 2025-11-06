async function login() {
  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value.trim();

  if (!email || !password) {
    alert('이메일과 비밀번호를 모두 입력해주세요.');
    return;
  }

  try {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });

    const result = await response.json();

    if (!response.ok || !result.success) {
      alert(result.message || '로그인 실패');
      return;
    }

    const { accessToken, refreshToken } = result.data;

    // JWT 토큰 로컬 저장
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);

    alert('로그인 성공');
    window.location.href = '/focus'; // 로그인 후 메인 페이지로 이동

  } catch (error) {
    console.error('로그인 요청 오류:', error);
    alert('서버 오류가 발생했습니다.');
  }
}
