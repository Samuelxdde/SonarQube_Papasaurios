function toggleSidebar() {
  document.querySelector(".navegacionadmin").classList.toggle("open");
}

function toggleSubmenu(event) {
  event.preventDefault();
  const parent = event.target.closest(".submenu");
  parent.classList.toggle("open");
}
