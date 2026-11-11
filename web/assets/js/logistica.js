(() => {
 const selector=document.getElementById('modalidad'); if(!selector)return;
 function mostrar(){document.querySelectorAll('[data-modalidad]').forEach(panel=>{
  const activo=panel.dataset.modalidad===selector.value;panel.hidden=!activo;
  panel.querySelectorAll('input,select').forEach(input=>{input.disabled=!activo;input.required=activo;});
 });}selector.addEventListener('change',mostrar);mostrar();
})();
