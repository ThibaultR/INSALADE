<?php
namespace AppBundle\Menu;

use Knp\Menu\FactoryInterface;
use Symfony\Component\DependencyInjection\ContainerAware;

class Builder extends ContainerAware
{
    public function mainMenu(FactoryInterface $factory, array $options)
    {
        $menu = $factory->createItem('root', array(
            'navbar' => true,
            'pull-right' => true,
        ));

        $securityContext = $this->container->get('security.context');

        if($securityContext->isGranted("ROLE_USER"))
        {
            $post = $menu->addChild('Posts', array(
                'route' => 'post'
            ));

            $userDropdown = $menu->addChild('User', array(
                'label' => $securityContext->getToken()->getUser()->getUsername(),
                'dropdown' => true,
                'caret' => true,
            ));

            $userDropdown->addChild('Logout', array('route' => 'fos_user_security_logout'));
            }

        else {
            $login = $menu->addChild('Login', array(
                'route' => 'fos_user_security_login'
            ));
        }

        return $menu;
    }
}